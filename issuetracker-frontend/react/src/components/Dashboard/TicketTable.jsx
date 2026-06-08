import React, { useState } from 'react';
import { NavLink, useSearchParams } from "react-router-dom"; 
import { StatusBadge, PriorityBadge } from "./Badge";
import TicketModal from '../TicketDetail/TicketModal';

export const TicketTable = ({ tickets, projectUuid}) => {
  const [searchParams, setSearchParams] = useSearchParams();

  const selectedTicketCode = searchParams.get('ticket');

  const [isTicketOpen, setIsTicketOpen] = useState(false);
  const [ticketInfo, setTicketInfo] = useState({});

  const handleCloseModal = () => {
    setSearchParams({});
  };

  return (
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
            <tr key={ticket.code}>
              <td>
                <div 
                  onClick={() => {setIsTicketOpen(true); setTicketInfo({projectUuid: ticket.projectUuid, code: ticket.code})}}
                  style={{ cursor: 'pointer' }}
                >
                  <div className="table-ticket-id" style={{ color: 'var(--hunter-green)', textDecoration: 'underline' }}>
                    {ticket.code}
                  </div>
                  <div className="table-ticket-title">{ticket.title}</div>
                </div>
              </td>

              <td>{ticket.assignee?.username ?? '—'}</td>

              <td>
                <PriorityBadge priority={ticket.ticketPriority} />
              </td>

              <td>
                <StatusBadge status={ticket.ticketStatus} />
              </td>
            </tr>
          ))}
          <tr>
            <td><NavLink to={`/create-ticket/${projectUuid}`} className="btn btn-success">Create new ticket</NavLink></td>
          </tr>
        </tbody>
      </table>

      {isTicketOpen && (
        <TicketModal 
          projectId={ticketInfo.projectUuid}
          ticketCode={ticketInfo.code} 
          onClose={() => {setIsTicketOpen(false)}} 
        />
      )}
    </div>
  );
};
