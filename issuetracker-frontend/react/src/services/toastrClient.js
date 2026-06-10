import toastr from "toastr";
import "toastr/build/toastr.min.css";

toastr.options.closeButton = true;
toastr.options.progressBar = true;
toastr.options.newestOnTop = true;
toastr.options.positionClass = "toast-top-right";
toastr.options.preventDuplicates = true;
toastr.options.timeOut = 7000;
toastr.options.extendedTimeOut = 1500;
export default toastr;
