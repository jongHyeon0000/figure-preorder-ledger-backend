package com.jong.figurepreorderledgerbackend.service.editor.impl;

import com.jong.figurepreorderledgerbackend.common.constant.GlobalExceptionCode;
import com.jong.figurepreorderledgerbackend.common.constant.UploadConstant;
import com.jong.figurepreorderledgerbackend.common.exception.BusinessException;
import com.jong.figurepreorderledgerbackend.config.properties.UploadProperties;
import com.jong.figurepreorderledgerbackend.mapper.editor.EditorImageMapper;
import com.jong.figurepreorderledgerbackend.service.editor.EditorImageService;
import com.jong.figurepreorderledgerbackend.vo.editor.EditorImageVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.Locale;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class EditorImageServiceImpl implements EditorImageService {

    private static final int HEADER_SIZE = 12;
    private static final int ORIGINAL_NAME_MAX = 255;

    private final EditorImageMapper editorImageMapper;
    private final UploadProperties uploadProperties;

    @Override
    @Transactional
    public EditorImageVO upload(Long userId, MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new BusinessException(GlobalExceptionCode.BAD_REQUEST, "업로드할 파일이 비어 있습니다");
        }
        String originalName = originalNameOf(file);
        String extension = extensionOf(originalName);
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

        String filePath = buildFilePath(userId, extension);
        editorImageMapper.insert(userId, filePath, originalName, file.getSize());
        Long editorImageId = editorImageMapper.selectLastInsertId();

        Path target = resolve(filePath);
        try {
            Files.createDirectories(target.getParent());
            try (InputStream in = file.getInputStream()) {
                Files.copy(in, target);
            }
        } catch (IOException e) {
            deleteQuietly(target);
            log.error("이미지 저장 실패: {}", filePath, e);
            throw new BusinessException(GlobalExceptionCode.INTERNAL_ERROR);   // 예외로 DB 행도 롤백된다
        }
        EditorImageVO result = new EditorImageVO();
        result.setEditorImageId(editorImageId);
        result.setFilePath(filePath);
        result.setOriginalName(originalName);
        result.setFileSize(file.getSize());
        result.setUrl(UploadConstant.URL_PREFIX + filePath);
        return result;
    }

    @Override
    public void checkNoBase64(String html) {
        for (Element img : parse(html).select("img[src]")) {
            if (img.attr("src").trim().toLowerCase(Locale.ROOT).startsWith("data:")) {   // 브라우저는 대소문자와 앞 공백을 무시한다
                throw new BusinessException(GlobalExceptionCode.BAD_REQUEST,
                        "설명에 base64 이미지를 넣을 수 없습니다. 이미지는 업로드해서 사용하세요");
            }
        }
    }

    private void deleteQuietly(Path target) {
        try {
            Files.deleteIfExists(target);
        } catch (IOException e) {
            log.warn("이미지 파일 정리 실패: {}", target, e);
        }
    }

    private Document parse(String html) {
        return Jsoup.parseBodyFragment(html == null ? "" : html);
    }

    /* 저장 경로는 서버가 만든다: {userId}/editor-image/{yyyy}/{MM}/{UUID}.{확장자} (원본 파일명은 경로에 쓰지 않는다) */
    private String buildFilePath(Long userId, String extension) {
        LocalDate today = LocalDate.now();
        return String.format("%d/%s/%d/%02d/%s.%s", userId, UploadConstant.EDITOR_IMAGE_DIR, today.getYear(),
                today.getMonthValue(), UUID.randomUUID(), extension);
    }

    /* 업로드 폴더 밖으로 나가는 경로는 허용하지 않는다 */
    private Path resolve(String filePath) {
        Path root = uploadProperties.getDir().toAbsolutePath().normalize();
        Path target = root.resolve(filePath).normalize();
        if (!target.startsWith(root)) {
            throw new BusinessException(GlobalExceptionCode.INTERNAL_ERROR);
        }
        return target;
    }

    private String originalNameOf(MultipartFile file) {
        String name = StringUtils.getFilename(file.getOriginalFilename());
        if (name == null) {
            name = "";
        }
        return name.length() > ORIGINAL_NAME_MAX ? name.substring(0, ORIGINAL_NAME_MAX) : name;
    }

    private String extensionOf(String fileName) {
        String extension = StringUtils.getFilenameExtension(fileName);
        return extension == null ? null : extension.toLowerCase(Locale.ROOT);
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
