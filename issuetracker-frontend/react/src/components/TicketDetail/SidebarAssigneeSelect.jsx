import React, { useState, useEffect } from 'react';
import { ticketService } from '../../services/ticketService';

const AssigneeSelect = ({ ticket, onUpdate }) => {
  const [users, setUsers] = useState([]);
  const [isUpdating, setIsUpdating] = useState(false);

  useEffect(() => {
    const fetchUsers = async () => {
      try {
        const data = await ticketService.getUsers();
        setUsers(data);
      } catch (error) {
        console.error('Грешка при зареждане на потребителите:', error);
      }
    };
    fetchUsers();
  }, []);

  const handleAssigneeChange = async (newUsername) => {
    setIsUpdating(true);
    try {
      // Подаваме кода на билета и новото име (или празен низ) към сървиса
      await ticketService.updateAssignee(ticket.code, newUsername);

      // Обновяваме локалния стейт на модала
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

  return (
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
  );
};

export default AssigneeSelect;