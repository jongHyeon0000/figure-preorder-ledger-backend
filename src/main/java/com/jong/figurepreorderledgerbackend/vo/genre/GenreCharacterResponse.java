package com.jong.figurepreorderledgerbackend.vo.genre;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class GenreCharacterResponse {

    private Long genreCharacterId;
    private Long genreCharacterGroupId;
    private String name;
}
