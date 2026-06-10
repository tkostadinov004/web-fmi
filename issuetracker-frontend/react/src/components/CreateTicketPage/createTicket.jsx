import { useEffect, useState } from "react";
import { useNavigate, useParams } from "react-router-dom";
import toastr from "../../services/toastrClient";
import axios_client from "../../axiosClient";
import { ticketService } from "../../services/ticketService";
import handleToastrError from "../../toastrUtils";

function CreateTicketPage() {
  const { projectUuid } = useParams();

  const navigate = useNavigate();

  const [users, setUsers] = useState([]);
  const [form, setForm] = useState({
    code: "",
    title: "",
    description: "",
    ticketPriority: "LOWEST",
    dueDate: "",
    assigneeUsername: "",
  });

  useEffect(() => {
    const fetchUsers = async () => {
      if (!projectUuid) return;

      try {
        const data = await ticketService.getUsers(projectUuid);
        setUsers(data);
      } catch (error) {
        handleToastrError(error);
      }
    };

    fetchUsers();
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
      const body = {
        code: form.code,
        title: form.title,
        description: form.description,
        ticketPriority: form.ticketPriority,
        dueDate: form.dueDate ? form.dueDate.replace("T", " ") : undefined,
        ...(form.assigneeUsername ? { assigneeUsername: form.assigneeUsername } : {}),
      };
      await axios_client.post(`/projects/${projectUuid}/tickets`, body);

      navigate(`/dashboard/${projectUuid}`);
    } catch (error) {
      handleToastrError(error);
    }
  };

  return (
    <div className="container py-5">
      <h2 className="mb-4">Create ticket</h2>

      <form className="card p-4 shadow" onSubmit={handleSubmit}>
        <div className="mb-3">
          <label className="form-label">Code</label>
          <input type="text" className="form-control" name="code" value={form.code} onChange={handleChange} required />
        </div>

        <div className="mb-3">
          <label className="form-label">Title</label>
          <input type="text" className="form-control" name="title" value={form.title} onChange={handleChange} required />
        </div>

        <div className="mb-3">
          <label className="form-label">Description</label>
          <textarea className="form-control" rows="5" name="description" value={form.description} onChange={handleChange} required />
        </div>

        <div className="row">
          <div className="col-md-6 mb-3">
            <label className="form-label">Priority</label>
            <select className="form-select" name="ticketPriority" value={form.ticketPriority} onChange={handleChange}>
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
          <input type="datetime-local" className="form-control" name="dueDate" value={form.dueDate} onChange={handleChange} />
        </div>

        <div className="mb-3">
          <label className="form-label">Assignee</label>
          <select className="form-select" name="assigneeUsername" value={form.assigneeUsername} onChange={handleChange}>
            <option value="">-- Не е зададен изпълнител --</option>
            {users.map((user) => (
              <option key={user.username} value={user.username}>
                {user.firstName} {user.lastName} ({user.username})
              </option>
            ))}
          </select>
        </div>

        <button className="btn btn-success mt-3" type="submit">
          Create Ticket
        </button>
      </form>
    </div>
  );
}

export default CreateTicketPage;
