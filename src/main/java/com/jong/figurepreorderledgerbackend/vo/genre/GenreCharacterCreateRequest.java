package com.jong.figurepreorderledgerbackend.vo.genre;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class GenreCharacterCreateRequest {

    @NotNull
    private Long genreId;

    @NotBlank
    @Size(max = 100)
    private String name;
}
