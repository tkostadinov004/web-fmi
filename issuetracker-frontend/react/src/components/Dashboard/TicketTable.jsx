import { NavLink } from "react-router-dom";
import { StatusBadge, PriorityBadge } from "./Badge";

export const TicketTable = ({ tickets }) => (
  <div className="table-container">
    <table>
      <thead>
        <tr>
          <th>Ticket</th>
          <th>Assignee</th>
          <th>Priority</th>
          <th>Status</th>
        </tr>
      </thead>

      <tbody>
        {tickets.map((ticket) => (
          <tr key={ticket.id}>
            <td>
              <NavLink to={`/ticket/${ticket.id}`}>
                <div className="table-ticket-id">{ticket.id}</div>
                <div className="table-ticket-title">{ticket.title}</div>
              </NavLink>
            </td>

            <td>{ticket.assignee}</td>

            <td>
              <PriorityBadge priority={ticket.priority} />
            </td>

            <td>
              <StatusBadge status={ticket.status} />
            </td>
          </tr>
        ))}
      </tbody>
    </table>
  </div>
);
