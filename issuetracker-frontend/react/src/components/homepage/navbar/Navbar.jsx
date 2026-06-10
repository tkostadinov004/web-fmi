import Container from "react-bootstrap/Container";
import NavbarBS from "react-bootstrap/Navbar";

import ProfileMenu from "./ProfileMenu";
import LoginRegisterBttns from "./LoginRegisterBttns";

function Navbar() {
  const username = localStorage.getItem("currentUsername");

  return (
    <NavbarBS bg="light" expand="lg" className="border-bottom mb-3">
      {" "}
      <Container>
        {/* <NavbarBS.Brand href="/">Issue Tracker</NavbarBS.Brand> */}
        <NavbarBS.Collapse className="justify-content-end">
          {username ? <ProfileMenu /> : <LoginRegisterBttns />}
        </NavbarBS.Collapse>
      </Container>
    </NavbarBS>
  );
}

export default Navbar;
