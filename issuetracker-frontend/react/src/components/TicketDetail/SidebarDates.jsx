import React from 'react';

const SidebarDates = ({ createDate, updateDate, dueDate }) => {
  const formatDate = (dateString) => {
    if (!dateString) return 'Няма данни';
    const date = new Date(dateString);
    return date.toLocaleDateString('bg-BG', { day: 'numeric', month: 'short', year: 'numeric' });
  };

  return (
    <div className="sidebar-group dates-group">
      <p>Създаден: <span>{formatDate(createDate)}</span></p>
      <p>Обновен: <span>{formatDate(updateDate)}</span></p>
      {dueDate && <p>Краен срок: <span>{formatDate(dueDate)}</span></p>}
    </div>
  );
};

export default SidebarDates;