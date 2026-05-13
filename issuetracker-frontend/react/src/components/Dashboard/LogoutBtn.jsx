import { useNavigate } from "react-router-dom";

function LogoutButton() {
  const navigate = useNavigate();

  const handleLogout = async () => {
    try {
      await fetch("http://localhost:8080/logout", {
        method: "POST",
        credentials: "include",
      });

    //   navigate("/login");
    } catch (error) {
      console.error("Logout failed:", error);
    }
  };

  return (
    <div className="logout-container">
      <button className="logout-button" onClick={handleLogout}>
        Logout
      </button>
    </div>
  );
}

export default LogoutButton;