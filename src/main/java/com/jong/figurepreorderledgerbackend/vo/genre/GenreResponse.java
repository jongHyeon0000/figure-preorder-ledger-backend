package com.jong.figurepreorderledgerbackend.vo.genre;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class GenreResponse {

    private Long id;
    private String name;
    private List<GenreCharacterResponse> children;
}
