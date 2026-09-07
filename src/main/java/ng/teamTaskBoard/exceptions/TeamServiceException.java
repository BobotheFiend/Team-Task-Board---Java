package ng.teamTaskBoard.exceptions;

public class TeamServiceException extends TeamTaskBoardException {
    public TeamServiceException(String message) {
        super(message);
    }
    public TeamServiceException(String message, Throwable cause) {
        super(message, cause);
    }
    public TeamServiceException(){
        super();
    }
    public TeamServiceException(Throwable cause) {
        super(cause);
    }
}
