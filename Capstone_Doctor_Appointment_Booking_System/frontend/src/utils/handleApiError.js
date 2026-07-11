// Common function to get the error message from API responses.

export function handleApiError(error) {
  return (
    error.response?.data?.message || "Something went wrong. Please try again."
  );
}
