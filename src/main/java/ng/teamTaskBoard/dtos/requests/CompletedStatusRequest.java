package ng.teamTaskBoard.dtos.requests;

import lombok.Data;

@Data
public class CompletedStatusRequest {

    private  String todoTitle;
    private String memberEmail;

}
