package com.jong.mikumikureservationbackend.service.common;

import com.jong.mikumikureservationbackend.common.constant.GlobalExceptionCode;
import com.jong.mikumikureservationbackend.common.exception.BusinessException;
import com.jong.mikumikureservationbackend.config.properties.UploadProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/*
 * 업로드 이미지 파일의 검증, 저장, 삭제를 한 곳에서 처리한다 (에디터 이미지, 썸네일, 고아 이미지 정리가 같이 쓴다).
 * 파일 경로(filePath)는 업로드 폴더(app.upload.dir) 기준의 상대 경로이며, 폴더 밖으로 나가는 경로는 쓰지 않는다.
 * 어디에 저장할지(경로 규칙)와 DB 처리는 호출하는 서비스가 맡는다.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ImageFileStore {

    private static final int HEADER_SIZE = 12;
    private static final int ORIGINAL_NAME_MAX = 255;

    private final UploadProperties uploadProperties;

    /*
     * 업로드 파일을 검사한다. 위반은 모두 400: 비어 있음, 허용하지 않는 확장자, 크기 초과,
     * 파일 내용(앞부분 바이트)이 확장자의 이미지가 아님
     */
    public void validate(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new BusinessException(GlobalExceptionCode.BAD_REQUEST, "업로드할 파일이 비어 있습니다");
        }
        String extension = extensionOf(originalNameOf(file));
        if (!uploadProperties.isAllowedExtension(extension)) {
            throw new BusinessException(GlobalExceptionCode.BAD_REQUEST,
                    "허용하지 않는 확장자입니다 (허용: " + uploadProperties.getAllowedExtensions() + ")");
        }
        if (file.getSize() > uploadProperties.getMaxSize().toBytes()) {
            throw new BusinessException(GlobalExceptionCode.BAD_REQUEST, "업로드 가능한 크기를 초과했습니다");
        }
        if (!matchesContent(extension, readHeader(file))) {
            throw new BusinessException(GlobalExceptionCode.BAD_REQUEST, "파일 내용이 확장자와 일치하는 이미지가 아닙니다");
        }
    }

    /* 클라이언트가 보낸 파일명에서 경로를 떼고 255자까지만 남긴다. DB에만 남기고 저장 경로에는 쓰지 않는다 */
    public String originalNameOf(MultipartFile file) {
        String name = StringUtils.getFilename(file.getOriginalFilename());
        if (name == null) {
            name = "";
        }
        return name.length() > ORIGINAL_NAME_MAX ? name.substring(0, ORIGINAL_NAME_MAX) : name;
    }

    /* 소문자 확장자. 없으면 null */
    public String extensionOf(String fileName) {
        String extension = StringUtils.getFilenameExtension(fileName);
        return extension == null ? null : extension.toLowerCase(Locale.ROOT);
    }

    /* 파일을 업로드 폴더의 filePath에 저장한다(폴더는 만든다). 실패하면 반쯤 쓴 파일을 지우고 500 */
    public void save(MultipartFile file, String filePath) {
        Path target = resolve(filePath);
        if (target == null) {
            throw new BusinessException(GlobalExceptionCode.INTERNAL_ERROR);
        }
        try {
            Files.createDirectories(target.getParent());
            try (InputStream in = file.getInputStream()) {
                Files.copy(in, target);
            }
        } catch (IOException e) {
            delete(filePath);
            log.error("이미지 파일 저장 실패: {}", filePath, e);
            throw new BusinessException(GlobalExceptionCode.INTERNAL_ERROR);   // 호출한 서비스의 DB 변경도 예외로 롤백된다
        }
    }

    /*
     * 파일을 지운다. 이미 없으면 성공으로 본다. 실패하거나 폴더 밖 경로면 지우지 않고 로그만 남기고 false를 돌려준다(예외 없음).
     * 실패를 신경 쓰지 않는 호출자는 반환값을 무시한다.
     */
    public boolean delete(String filePath) {
        Path target = resolve(filePath);
        if (target == null) {
            log.warn("업로드 폴더 밖 경로라 지우지 않음: {}", filePath);
            return false;
        }
        try {
            Files.deleteIfExists(target);
            return true;
        } catch (IOException e) {
            log.warn("이미지 파일 삭제 실패: {}", target, e);
            return false;
        }
    }

    /* 업로드 폴더 아래 폴더 하나를 통째로 지운다(탈퇴 때 {userId}). 업로드 폴더 밖이거나 실패하면 false(예외 없음) */
    public boolean deleteDirectory(String dirPath) {
        Path target = resolve(dirPath);
        Path root = uploadProperties.getDir().toAbsolutePath().normalize();
        if (target == null || target.equals(root)) {
            log.warn("업로드 폴더 밖이거나 업로드 폴더 자체라 지우지 않음: {}", dirPath);
            return false;
        }
        if (!Files.exists(target)) {
            return true;
        }
        try (Stream<Path> paths = Files.walk(target)) {
            // 깊은 경로부터 지운다
            List<Path> deleteOrder = paths.sorted(Comparator.reverseOrder()).collect(Collectors.toList());
            for (Path path : deleteOrder) {
                Files.deleteIfExists(path);
            }
            return true;
        } catch (IOException e) {
            log.warn("업로드 폴더 삭제 실패: {}", target, e);
            return false;
        }
    }

    /* 업로드 폴더 기준 절대 경로. 폴더 밖으로 나가면 null */
    private Path resolve(String filePath) {
        Path root = uploadProperties.getDir().toAbsolutePath().normalize();
        Path target = root.resolve(filePath).normalize();
        return target.startsWith(root) ? target : null;
    }

    private byte[] readHeader(MultipartFile file) {
        try (InputStream in = file.getInputStream()) {
            return in.readNBytes(HEADER_SIZE);
        } catch (IOException e) {
            throw new BusinessException(GlobalExceptionCode.BAD_REQUEST, "파일을 읽을 수 없습니다");
        }
    }

    /* 확장자별로 파일 앞부분(매직 바이트)이 실제 그 형식인지 확인한다 */
    private boolean matchesContent(String extension, byte[] header) {
        switch (extension) {
            case "jpg":
            case "jpeg":
                return header.length >= 3
                        && (header[0] & 0xFF) == 0xFF && (header[1] & 0xFF) == 0xD8 && (header[2] & 0xFF) == 0xFF;
            case "png":
                return header.length >= 8
                        && (header[0] & 0xFF) == 0x89 && startsWith(header, 1, "PNG")
                        && (header[4] & 0xFF) == 0x0D && (header[5] & 0xFF) == 0x0A
                        && (header[6] & 0xFF) == 0x1A && (header[7] & 0xFF) == 0x0A;
            case "gif":
                return startsWith(header, 0, "GIF87a") || startsWith(header, 0, "GIF89a");
            case "webp":
                return startsWith(header, 0, "RIFF") && startsWith(header, 8, "WEBP");
            default:
                return false;
        }
    }

    private boolean startsWith(byte[] header, int offset, String ascii) {
        byte[] expected = ascii.getBytes(StandardCharsets.US_ASCII);
        if (header.length < offset + expected.length) {
            return false;
        }
        for (int i = 0; i < expected.length; i++) {
            if (header[offset + i] != expected[i]) {
                return false;
            }
        }
        return true;
    }
}
