import React, { useState, useEffect } from 'react';

const STATUS_OPTIONS = ['TO_DO', 'IN_PROGRESS', 'DONE'];
const PRIORITY_OPTIONS = ['LOWEST', 'LOW', 'MEDIUM', 'HIGH', 'HIGHEST'];

const TicketSidebar = ({ ticket, onUpdate }) => {
  const [users, setUsers] = useState([]);
  const [isUpdating, setIsUpdating] = useState(false);

  useEffect(() => {
    const fetchUsers = async () => {
      try {
        const token = localStorage.getItem('authToken');
        const response = await fetch('http://localhost:8080/users?page_number=1&page_size=100', {
          headers: { 'Authorization': `Bearer ${token}` }
        });
        
        if (response.ok) {
          const data = await response.json();
          setUsers(data); 
        }
      } catch (error) {
        console.error('Грешка при зареждане на потребителите:', error);
      }
    };

    fetchUsers();
  }, []);

  // Обща функция за обновяване на Статус или Приоритет (PATCH /tickets/{code})
  const handleGeneralUpdate = async (field, value) => {
    setIsUpdating(true);
    try {
      const token = localStorage.getItem('authToken');
      const response = await fetch(`http://localhost:8080/tickets/${ticket.code}`, {
        method: 'PATCH',
        headers: {
          'Content-Type': 'application/json',
          'Authorization': `Bearer ${token}`
        },
        body: JSON.stringify({ [field]: value })
      });

      if (!response.ok) throw new Error('Грешка при обновяване');
      
      onUpdate({ ...ticket, [field]: value });
    } catch (error) {
      alert('Не успяхме да запазим промяната.');
    } finally {
      setIsUpdating(false);
    }
  };

  const handleAssigneeChange = async (newUsername) => {
    setIsUpdating(true);
    try {
      const token = localStorage.getItem('authToken');
      let response;

      if (newUsername === "") {
        response = await fetch(`http://localhost:8080/tickets/${ticket.code}/assignee`, {
          method: 'DELETE',
          headers: { 'Authorization': `Bearer ${token}` }
        });
      } else {
        response = await fetch(`http://localhost:8080/tickets/${ticket.code}/assignee?assigneeUsername=${newUsername}`, {
          method: 'PATCH',
          headers: { 'Authorization': `Bearer ${token}` }
        });
      }

      if (!response.ok) throw new Error('Грешка при промяна на изпълнител');

      if (newUsername === "") {
        onUpdate({ ...ticket, assignee: null });
      } else {
        const selectedUser = users.find(u => u.username === newUsername);
        onUpdate({ ...ticket, assignee: selectedUser });
      }

    } catch (error) {
      console.error(error);
      alert('Не успяхме да променим изпълнителя.');
    } finally {
      setIsUpdating(false);
    }
  };

  const formatDate = (dateString) => {
    if (!dateString) return 'Няма данни';
    const date = new Date(dateString);
    return date.toLocaleDateString('bg-BG', { day: 'numeric', month: 'short', year: 'numeric' });
  };

  return (
    <div className="modal-sidebar">
      
      <div className="sidebar-group">
        <label>Статус</label>
        <select 
          className="sidebar-select status-select"
          value={ticket.ticketStatus}
          onChange={(e) => handleGeneralUpdate('ticketStatus', e.target.value)}
          disabled={isUpdating}
        >
          {STATUS_OPTIONS.map(status => (
            <option key={status} value={status}>{status.replace('_', ' ')}</option>
          ))}
        </select>
      </div>

      <div className="sidebar-group">
        <label>Приоритет</label>
        <select 
          className="sidebar-select"
          value={ticket.ticketPriority}
          onChange={(e) => handleGeneralUpdate('ticketPriority', e.target.value)}
          disabled={isUpdating}
        >
          {PRIORITY_OPTIONS.map(priority => (
            <option key={priority} value={priority}>{priority}</option>
          ))}
        </select>
      </div>

      <div className="sidebar-group">
        <label>Изпълнител</label>
        <select 
          className="sidebar-select"
          value={ticket.assignee?.username || ''}
          onChange={(e) => handleAssigneeChange(e.target.value)}
          disabled={isUpdating}
        >
          <option value="">-- Неразпределен --</option>
          {users.map(user => (
            <option key={user.username} value={user.username}>
              {user.firstName} {user.lastName} ({user.username})
            </option>
          ))}
        </select>
      </div>

      <div className="sidebar-group dates-group">
        <p>Създаден: <span>{formatDate(ticket.createDate)}</span></p>
        <p>Обновен: <span>{formatDate(ticket.updateDate)}</span></p>
        {ticket.dueDate && <p>Краен срок: <span>{formatDate(ticket.dueDate)}</span></p>}
      </div>

    </div>
  );
};

export default TicketSidebar;