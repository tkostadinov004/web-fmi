package bg.sofia.uni.fmi.issuetracker.response;

public record InvalidOrExpiredTokenErrorResponse(String message, boolean expired) {
}
