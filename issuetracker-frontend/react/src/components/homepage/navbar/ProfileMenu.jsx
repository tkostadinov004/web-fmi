import Dropdown from "react-bootstrap/Dropdown";
import { NavLink } from "react-router-dom";
import LogoutButton from "./LogoutBtn";
import CreateProjectPage from "../../CreateProjectPage/createProject";

function ProfileMenu() {
  const username = localStorage.getItem("currentUsername");

  return (
    <Dropdown align="end">
      <Dropdown.Toggle variant="light">{username}</Dropdown.Toggle>

      <Dropdown.Menu>
        <LogoutButton />
      </Dropdown.Menu>
    </Dropdown>
  );
}

export default ProfileMenu;
