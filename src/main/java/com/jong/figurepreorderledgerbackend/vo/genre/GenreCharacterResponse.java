package com.jong.figurepreorderledgerbackend.vo.genre;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class GenreCharacterResponse {

    private Long id;
    private Long genreId;
    private String name;
}
