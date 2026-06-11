import toastr from "./services/toastrClient";

function handleToastrError(error) {
  if (error.response?.data?.error) {
    toastr.error(error.response.data.error);
  } else if (error.response?.data?.errors) {
    Object.entries(error.response.data.errors).forEach((e) => toastr.error(`Invalid ${e[0]}: ${e[1]}`));
  } else {
    toastr.error("Unexpected server error!");
  }
}

export default handleToastrError;
