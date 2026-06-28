package com.wimbledon.dto;
import lombok.Builder;
import lombok.Data;
@Data @Builder
public class MatchResultDto {
    private String winner;
    private Integer setsWinner;
    private Integer gamesWinner;
    private Integer gamesLoser;
}
