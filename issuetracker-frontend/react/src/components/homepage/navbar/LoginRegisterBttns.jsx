// import { NavLink } from "react-router-dom";

// const LoginRegisterBttns = () => {
//   return (
//     <>
//       <div className="text-end">
//         <NavLink to="/login" type="button" className="btn btn-outline-light me-2">
//           Login
//         </NavLink>
//         <NavLink to="/register" type="button" className="btn btn-warning">
//           Sign-up
//         </NavLink>
//       </div>
//     </>
//   );
// };

// export default LoginRegisterBttns;

import Button from "react-bootstrap/Button";
import { NavLink } from "react-router-dom";

function LoginRegisterBttns() {
  return (
    <div className="d-flex gap-2">
      <Button as={NavLink} to="/login" variant="outline-primary">
        Login
      </Button>

      <Button as={NavLink} to="/register" variant="warning">
        Sign Up
      </Button>
    </div>
  );
}

export default LoginRegisterBttns;
