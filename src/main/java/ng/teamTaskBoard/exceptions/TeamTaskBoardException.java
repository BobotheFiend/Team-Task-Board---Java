package ng.teamTaskBoard.exceptions;

public class TeamTaskBoardException extends Exception {
    public TeamTaskBoardException(String message) {
        super(message);
    }
    public TeamTaskBoardException(String message, Throwable cause) {
        super(message, cause);
    }
    public TeamTaskBoardException(){
        super();
    }
    public TeamTaskBoardException(Throwable cause) {
        super(cause);
    }
}
