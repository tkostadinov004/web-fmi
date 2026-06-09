// import { NavLink } from "react-router-dom";
// import LogoutButton from "./LogoutBtn";

// const ProfileMenu = () => {
//   const username = localStorage.getItem("currentUsername");
//   return (
//     <>
//       <div className="dropdown text-end">
//         <div
//           className="d-flex align-items-center dropdown-toggle"
//           role="button"
//           data-bs-toggle="dropdown"
//           aria-expanded="false"
//         >
//           <span className="me-2">{username}</span>
//           {/* <img
//             src={avatarUrl}
//             alt="avatar"
//             width="32"
//             height="32"
//             className="rounded-circle"
//           /> */}
//         </div>
//         <ul className="dropdown-menu text-small">
//           <li>
//             <NavLink to="/create-project" className="dropdown-item">
//               Create new project...
//             </NavLink>
//           </li>
//           <li>
//             <hr className="dropdown-divider" />
//           </li>
//           <li>
//             <LogoutButton></LogoutButton>
//           </li>
//         </ul>
//       </div>
//     </>
//   );
// };

// export default ProfileMenu;
import Dropdown from "react-bootstrap/Dropdown";
import { NavLink } from "react-router-dom";
import LogoutButton from "./LogoutBtn";

function ProfileMenu() {
  const username = localStorage.getItem("currentUsername");

  return (
    <Dropdown align="end">
      <Dropdown.Toggle variant="light">{username}</Dropdown.Toggle>

      <Dropdown.Menu>
        <Dropdown.Item as={NavLink} to="/create-project">
          Create new project...
        </Dropdown.Item>

        <Dropdown.Divider />

        <LogoutButton />
      </Dropdown.Menu>
    </Dropdown>
  );
}

export default ProfileMenu;
