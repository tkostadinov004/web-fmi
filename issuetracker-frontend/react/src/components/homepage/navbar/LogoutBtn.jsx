// import { useNavigate } from "react-router-dom";

// const getHeaders = () => {
//     const token = localStorage.getItem('authToken');
//     return {
//         'Content-Type': 'application/json',
//         'Authorization': `Bearer ${token}`
//     };
// };

// function LogoutButton() {
//   const navigate = useNavigate();

//   const handleLogout = async () => {
//     try {
//       await fetch("http://localhost:8080/auth/logout", {
//         method: "POST",
//         headers: getHeaders()
//       });

//       localStorage.clear();
//       navigate("/");
//     } catch (error) {
//       console.error("Logout failed:", error);
//     }
//   };

//   return (
//     <div className="logout-container">
//       <button className="logout-button" onClick={handleLogout}>
//         Logout
//       </button>
//     </div>
//   );
// }

// export default LogoutButton;

import Button from "react-bootstrap/Button";
import Dropdown from "react-bootstrap/Dropdown";
import { useNavigate } from "react-router-dom";

const getHeaders = () => {
  const token = localStorage.getItem("authToken");

  return {
    "Content-Type": "application/json",
    Authorization: `Bearer ${token}`,
  };
};

function LogoutButton() {
  const navigate = useNavigate();

  const handleLogout = async () => {
    try {
      await fetch("http://localhost:8080/auth/logout", {
        method: "POST",
        headers: getHeaders(),
      });

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
