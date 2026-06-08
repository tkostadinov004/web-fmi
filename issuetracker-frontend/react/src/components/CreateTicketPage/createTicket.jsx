import { useEffect, useState } from "react";
import { useNavigate, useParams } from "react-router-dom";

const getHeaders = () => {
  const token = localStorage.getItem("authToken");
  return {
    "Content-Type": "application/json",
    Authorization: `Bearer ${token}`,
  };
};

function CreateTicketPage() {
  const { projectUuid } = useParams();
  const username = localStorage.getItem("currentUsername");

  const navigate = useNavigate();

  const [form, setForm] = useState({
    code: "",
    title: "",
    description: "",
    ticketStatus: "OPEN",
    ticketPriority: "LOWEST",
    dueDate: "",
    assigneeUsername: username,
  });

  useEffect(() => {
    const fetchUser = async () => {
      //   const res = await fetch(`http://localhost:3000/users/${projectUuid}`);
      //   const data = await res.json();
      //   console.log(data);
      //   setForm(data);
    };

    fetchUser();
  }, [projectUuid]);

  const handleChange = (e) => {
    setForm({
      ...form,
      [e.target.name]: e.target.value,
    });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    try {
      const res = await fetch(
        `http://localhost:8080/projects/${projectUuid}/tickets`,
        {
          method: "POST",
          headers: getHeaders(),
          body: JSON.stringify(form),
        },
      );

      console.log("adta:",JSON.stringify(form));
      console.log("Status:", res.status);
      console.log("URL:", res.url);

      const text = await res.text();
      console.log(text);

      if (res.ok) {
        navigate(`/dashboard/${projectUuid}`);
      }
    } catch (error) {
      console.log("Error on creating ticket");
    }
  };

  return (
    <div className="container py-5">
      <h2 className="mb-4">Create ticket</h2>

      <form className="card p-4 shadow" onSubmit={handleSubmit}>
        <div className="mb-3">
          <label className="form-label">Code</label>
          <input
            type="text"
            className="form-control"
            name="code"
            value={form.code}
            onChange={handleChange}
            required
          />
        </div>

        <div className="mb-3">
          <label className="form-label">Title</label>
          <input
            type="text"
            className="form-control"
            name="title"
            value={form.title}
            onChange={handleChange}
            required
          />
        </div>

        <div className="mb-3">
          <label className="form-label">Description</label>
          <textarea
            className="form-control"
            rows="5"
            name="description"
            value={form.description}
            onChange={handleChange}
            required
          />
        </div>

        <div className="row">
          <div className="col-md-6 mb-3">
            <label className="form-label">Status</label>
            <select
              className="form-select"
              name="ticketStatus"
              value={form.ticketStatus}
              onChange={handleChange}
            >
              <option value="OPEN">Open</option>
              <option value="IN_PROGRESS">In Progress</option>
              <option value="RESOLVED">Resolved</option>
              <option value="CLOSED">Closed</option>
            </select>
          </div>

          <div className="col-md-6 mb-3">
            <label className="form-label">Priority</label>
            <select
              className="form-select"
              name="ticketPriority"
              value={form.ticketPriority}
              onChange={handleChange}
            >
              <option value="LOWEST">Lowest</option>
              <option value="LOW">Low</option>
              <option value="MEDIUM">Medium</option>
              <option value="HIGH">High</option>
              <option value="HIGHEST">Highest</option>
            </select>
          </div>
        </div>

        <div className="mb-3">
          <label className="form-label">Due Date</label>
          <input
            type="datetime-local"
            className="form-control"
            name="dueDate"
            value={form.dueDate}
            onChange={handleChange}
          />
        </div>

        <div className="mb-3">
          <label className="form-label">Assignee Username</label>
          <input className="form-control" value={username || ""} disabled />
          {/* <input
            type="text"
            className="form-control"
            name="assigneeUsername"
            value={form.assigneeUsername}
            onChange={handleChange}
          /> */}
          {/* <select
            className="form-select"
            name="assigneeUsername"
            value={form.assigneeUsername}
            onChange={handleChange}
          >
            <option value="">Unassigned</option>

            {users.map((user) => (
              <option key={user.username} value={user.username}>
                {user.firstName} {user.lastName}
              </option>
            ))}
          </select> */}
        </div>

        <button className="btn btn-success mt-3" type="submit">
          Create Ticket
        </button>
      </form>
    </div>
  );
}

export default CreateTicketPage;
