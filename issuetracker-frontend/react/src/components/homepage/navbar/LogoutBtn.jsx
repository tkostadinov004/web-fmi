import Button from "react-bootstrap/Button";
import Dropdown from "react-bootstrap/Dropdown";
import { useNavigate } from "react-router-dom";
import axios_client from "../../../axiosClient";

function LogoutButton() {
  const navigate = useNavigate();

  const handleLogout = async () => {
    try {
      await axios_client.post("/auth/logout");

      localStorage.clear();
      navigate("/login");
    } catch (error) {
      console.error("Logout failed:", error);
    }
  };

  return (
    <Dropdown.Item as="button" onClick={handleLogout}>
      Logout
    </Dropdown.Item>
  );
}

export default LogoutButton;
