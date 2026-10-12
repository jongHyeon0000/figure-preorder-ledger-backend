package com.jong.figurepreorderledgerbackend.service.thumbnail.impl;

import com.jong.figurepreorderledgerbackend.common.constant.GlobalExceptionCode;
import com.jong.figurepreorderledgerbackend.common.constant.ThumbnailTarget;
import com.jong.figurepreorderledgerbackend.common.constant.UploadConstant;
import com.jong.figurepreorderledgerbackend.common.exception.BusinessException;
import com.jong.figurepreorderledgerbackend.config.properties.UploadProperties;
import com.jong.figurepreorderledgerbackend.mapper.figure.FigureMapper;
import com.jong.figurepreorderledgerbackend.mapper.genre.GenreCharacterGroupMapper;
import com.jong.figurepreorderledgerbackend.mapper.genre.GenreCharacterMapper;
import com.jong.figurepreorderledgerbackend.mapper.maker.MakerMapper;
import com.jong.figurepreorderledgerbackend.mapper.series.SeriesGroupMapper;
import com.jong.figurepreorderledgerbackend.mapper.series.SeriesMapper;
import com.jong.figurepreorderledgerbackend.mapper.shop.ShopGroupMapper;
import com.jong.figurepreorderledgerbackend.mapper.shop.ShopMapper;
import com.jong.figurepreorderledgerbackend.mapper.thumbnail.ThumbnailImageMapper;
import com.jong.figurepreorderledgerbackend.mapper.type.FigureTypeGroupMapper;
import com.jong.figurepreorderledgerbackend.mapper.type.FigureTypeMapper;
import com.jong.figurepreorderledgerbackend.service.thumbnail.ThumbnailImageService;
import com.jong.figurepreorderledgerbackend.vo.thumbnail.ThumbnailImageVO;
import com.jong.figurepreorderledgerbackend.vo.thumbnail.ThumbnailUrlResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
import java.util.List;
import java.util.Locale;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ThumbnailImageServiceImpl implements ThumbnailImageService {

    private static final int HEADER_SIZE = 12;
    private static final int ORIGINAL_NAME_MAX = 255;

    private final ThumbnailImageMapper thumbnailImageMapper;
    private final UploadProperties uploadProperties;

    // 대상이 내 것인지 확인할 때 쓰는 종류별 매퍼
    private final FigureMapper figureMapper;
    private final ShopGroupMapper shopGroupMapper;
    private final ShopMapper shopMapper;
    private final FigureTypeGroupMapper figureTypeGroupMapper;
    private final FigureTypeMapper figureTypeMapper;
    private final SeriesGroupMapper seriesGroupMapper;
    private final SeriesMapper seriesMapper;
    private final GenreCharacterGroupMapper genreCharacterGroupMapper;
    private final GenreCharacterMapper genreCharacterMapper;
    private final MakerMapper makerMapper;

    @Override
    @Transactional
    public ThumbnailUrlResponse upload(Long userId, ThumbnailTarget target, Long targetId, MultipartFile file) {
        checkTargetExists(userId, target, targetId);
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

        // 이미 썸네일이 있으면 행을 지우고 새로 넣는다(대상당 1개). 옛 파일은 새 파일 저장에 성공한 뒤 지운다
        ThumbnailImageVO old = thumbnailImageMapper.selectByTarget(userId, target, targetId);
        if (old != null) {
            thumbnailImageMapper.deleteByTarget(userId, target, targetId);
        }
        String filePath = buildFilePath(userId, target, extension);
        thumbnailImageMapper.insert(userId, target, targetId, filePath, originalName, file.getSize());

        Path path = resolve(filePath);
        try {
            Files.createDirectories(path.getParent());
            try (InputStream in = file.getInputStream()) {
                Files.copy(in, path);
            }
        } catch (IOException e) {
            deleteQuietly(path);
            log.error("썸네일 저장 실패: {}", filePath, e);
            throw new BusinessException(GlobalExceptionCode.INTERNAL_ERROR);   // 예외로 DB 변경도 롤백된다
        }
        if (old != null) {
            deleteQuietly(resolve(old.getFilePath()));
        }
        return new ThumbnailUrlResponse(UploadConstant.URL_PREFIX + filePath);
    }

    @Override
    @Transactional
    public void delete(Long userId, ThumbnailTarget target, Long targetId) {
        checkTargetExists(userId, target, targetId);
        ThumbnailImageVO old = thumbnailImageMapper.selectByTarget(userId, target, targetId);
        if (old != null) {
            thumbnailImageMapper.deleteByTarget(userId, target, targetId);
            deleteQuietly(resolve(old.getFilePath()));
        }
    }

    @Override
    @Transactional
    public void deleteByTargets(Long userId, ThumbnailTarget target, List<Long> targetIds) {
        if (targetIds == null || targetIds.isEmpty()) {
            return;
        }
        List<String> filePaths = thumbnailImageMapper.selectFilePathsByTargets(userId, target, targetIds);
        if (filePaths.isEmpty()) {
            return;
        }
        thumbnailImageMapper.deleteByTargets(userId, target, targetIds);
        for (String filePath : filePaths) {
            deleteQuietly(resolve(filePath));
        }
    }

    /* 종류에 맞는 테이블에서 대상이 현재 사용자의 것인지 확인한다. 없거나 남의 것이면 404 */
    private void checkTargetExists(Long userId, ThumbnailTarget target, Long targetId) {
        boolean exists;
        switch (target) {
            case FIGURE:
                exists = figureMapper.selectCurrentStatus(userId, targetId) != null;
                break;
            case SHOP_GROUP:
                exists = shopGroupMapper.selectById(userId, targetId) != null;
                break;
            case SHOP:
                exists = shopMapper.selectById(userId, targetId) != null;
                break;
            case FIGURE_TYPE_GROUP:
                exists = figureTypeGroupMapper.selectById(userId, targetId) != null;
                break;
            case FIGURE_TYPE:
                exists = figureTypeMapper.selectById(userId, targetId) != null;
                break;
            case SERIES_GROUP:
                exists = seriesGroupMapper.selectById(userId, targetId) != null;
                break;
            case SERIES:
                exists = seriesMapper.selectById(userId, targetId) != null;
                break;
            case GENRE_CHARACTER_GROUP:
                exists = genreCharacterGroupMapper.selectById(userId, targetId) != null;
                break;
            case GENRE_CHARACTER:
                exists = genreCharacterMapper.selectById(userId, targetId) != null;
                break;
            case MAKER:
                exists = makerMapper.selectById(userId, targetId) != null;
                break;
            default:
                exists = false;
        }
        if (!exists) {
            throw new BusinessException(GlobalExceptionCode.NOT_FOUND);
        }
    }

    private void deleteQuietly(Path path) {
        try {
            Files.deleteIfExists(path);
        } catch (IOException e) {
            log.warn("썸네일 파일 정리 실패: {}", path, e);
        }
    }

    /* 저장 경로는 서버가 만든다: {userId}/thumbnail-image/{대상 종류}/{yyyy}/{MM}/{UUID}.{확장자} (원본 파일명은 경로에 쓰지 않는다) */
    private String buildFilePath(Long userId, ThumbnailTarget target, String extension) {
        LocalDate today = LocalDate.now();
        return String.format("%d/%s/%s/%d/%02d/%s.%s", userId, UploadConstant.THUMBNAIL_IMAGE_DIR, target.getUrlValue(),
                today.getYear(), today.getMonthValue(), UUID.randomUUID(), extension);
    }

    /* 업로드 폴더 밖으로 나가는 경로는 허용하지 않는다 */
    private Path resolve(String filePath) {
        Path root = uploadProperties.getDir().toAbsolutePath().normalize();
        Path path = root.resolve(filePath).normalize();
        if (!path.startsWith(root)) {
            throw new BusinessException(GlobalExceptionCode.INTERNAL_ERROR);
        }
        return path;
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
