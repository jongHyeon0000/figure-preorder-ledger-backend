package com.jong.mikumikureservationbackend.service.editor.impl;

import com.jong.mikumikureservationbackend.common.constant.GlobalExceptionCode;
import com.jong.mikumikureservationbackend.common.constant.UploadConstant;
import com.jong.mikumikureservationbackend.common.exception.BusinessException;
import com.jong.mikumikureservationbackend.mapper.editor.EditorImageMapper;
import com.jong.mikumikureservationbackend.service.common.ImageFileStore;
import com.jong.mikumikureservationbackend.service.editor.EditorImageService;
import com.jong.mikumikureservationbackend.vo.editor.EditorImageVO;
import lombok.RequiredArgsConstructor;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class EditorImageServiceImpl implements EditorImageService {

    private final EditorImageMapper editorImageMapper;
    private final ImageFileStore imageFileStore;

    @Override
    @Transactional
    public EditorImageVO upload(Long userId, MultipartFile file) {
        imageFileStore.validate(file);
        String originalName = imageFileStore.originalNameOf(file);
        String extension = imageFileStore.extensionOf(originalName);

        String filePath = buildFilePath(userId, extension);
        editorImageMapper.insert(userId, filePath, originalName, file.getSize());
        Long editorImageId = editorImageMapper.selectLastInsertId();

        imageFileStore.save(file, filePath);   // 실패하면 예외로 DB 행도 롤백된다
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

    @Override
    public List<String> extractFilePaths(String html) {
        Set<String> filePaths = new LinkedHashSet<>();
        for (Element img : parse(html).select("img[src]")) {
            String src = img.attr("src").trim();
            if (src.startsWith(UploadConstant.URL_PREFIX)) {
                filePaths.add(src.substring(UploadConstant.URL_PREFIX.length()));
            }
        }
        return new ArrayList<>(filePaths);
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
}
