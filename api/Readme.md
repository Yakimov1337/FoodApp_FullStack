# Error Handling Documentation

This documentation provides an overview of the different error responses returned by the application and the conditions under which they are triggered.

## Error Responses

| **Error**                                | **HTTP Status Code** | **Description**                                           | **Condition**                                                                 |
|------------------------------------------|----------------------|-----------------------------------------------------------|------------------------------------------------------------------------------|
| `Invalid JSON input`                     | 400 (Bad Request)    | The JSON input is malformed or invalid.                   | Triggered when a `HttpMessageNotReadableException` is thrown.                |
| `Email already exists`                   | 409 (Conflict)       | The email provided during registration is already in use. | Triggered when a `IllegalArgumentException` is thrown with this message.     |
| `Authentication failed: {error message}` | 401 (Unauthorized)   | General authentication failure.                           | Triggered by `CustomAuthenticationEntryPoint` when authentication fails.     |
| `Access Denied: {error message}`         | 403 (Forbidden)      | The user does not have permission to access the resource. | Triggered by `CustomAccessDeniedHandler` when access is denied.              |
| `Invalid or expired token`               | 401 (Unauthorized)   | The provided token is invalid or has expired.             | Triggered when a `JwtException` is caught.                                   |
| `Token has expired`                      | 401 (Unauthorized)   | The provided token has expired.                           | Triggered when an `ExpiredJwtException` is caught.                           |
| `Invalid email or password`              | 400 (Bad Request)    | The email or password provided during login is incorrect. | Triggered when a `IllegalArgumentException` is thrown with this message.     |
| `Passwords do not match`                 | 400 (Bad Request)    | The password and confirm password fields do not match.    | Triggered when a password mismatch is detected during registration.          |
| `User not found`                         | 404 (Not Found)      | The specified user could not be found.                    | Triggered when a `IllegalArgumentException` is thrown with this message.     |
| `Order not found`                        | 404 (Not Found)      | The specified order could not be found.                   | Triggered when a `IllegalArgumentException` is thrown with this message.     |
| `Menu Item not found`                    | 404 (Not Found)      | The specified menu item could not be found.               | Triggered when a `IllegalArgumentException` is thrown with this message.     |
| `Invalid or expired refresh token`       | 403 (Forbidden)      | The provided refresh token is invalid or expired.         | Triggered when an invalid or expired refresh token is detected.              |
| `An unexpected error occurred: {error}`  | 500 (Internal Server Error) | A general, unexpected server error.                       | Triggered by a catch-all `Exception` handler.                                |
| `Field validation errors`                | 400 (Bad Request)    | One or more fields failed validation.                     | Triggered when a `MethodArgumentNotValidException` is thrown.                |

## Exception Handling