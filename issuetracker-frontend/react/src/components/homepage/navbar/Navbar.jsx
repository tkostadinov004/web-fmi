import { NavLink } from "react-router-dom";
import ProfileMenu from "./ProfileMenu";
import Login_RegisterBttns from "./LoginRegisterBttns";

function Navbar({ isLogged } ) {
  const username = localStorage.getItem('currentUsername');
  return (
    <header className="p-3 mb-3 border-bottom">
      <div className="container">
        <div className="d-flex flex-wrap align-items-center justify-content-center justify-content-lg-start">
          <ul className="nav col-12 col-lg-auto me-lg-auto mb-2 justify-content-center mb-md-0">
            <li>   </li>
          </ul>
          
          {/* <ProfileMenu/> */}
          {/* <Login_RegisterBttns></Login_RegisterBttns> */}
          {username? <ProfileMenu/>: <Login_RegisterBttns/>}
        </div>{" "}
      </div>{" "}
    </header>
  );
}

export default Navbar;
