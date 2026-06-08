import { NavLink } from "react-router-dom";

const LoginRegisterBttns = () => {
  return (
    <>
      <div className="text-end">
        <NavLink to="/login" type="button" className="btn btn-outline-light me-2">
          Login
        </NavLink>
        <NavLink to="/register" type="button" className="btn btn-warning">
          Sign-up
        </NavLink>
      </div>
    </>
  );
};

export default LoginRegisterBttns;