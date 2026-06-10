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
