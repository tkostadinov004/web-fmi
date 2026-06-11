import React from 'react';

const SidebarDates = ({ createDate, updateDate, dueDate, onDueDateChange, isUpdating }) => {

  const formatForDisplay = (dateString) => {
    if (!dateString) return 'Няма данни';
    const date = new Date(dateString);
    return date.toLocaleString('bg-BG', {
      day: 'numeric', month: 'short', year: 'numeric',
      hour: '2-digit', minute: '2-digit'
    });
  };

  const formatForInput = (dateString) => {
    if (!dateString) return '';
    const formatted = dateString.replace(' ', 'T');
    return formatted.substring(0, 16);
  };

  const handleDateChange = (e) => {
    const selectedDate = e.target.value;

    if (selectedDate) {
      const formattedForBackend = selectedDate.replace('T', ' ');
      onDueDateChange(formattedForBackend);
    } else {
      onDueDateChange(null);
    }
  };

  return (
    <div className="sidebar-group dates-group">
      <p>Създаден: <span>{formatForDisplay(createDate)}</span></p>
      <p>Обновен: <span>{formatForDisplay(updateDate)}</span></p>
      <div className="editable-date-row">
        <span>Краен срок: </span>
        <input
          type="datetime-local"
          className="sidebar-date-input"
          value={formatForInput(dueDate)}
          onChange={handleDateChange}
          disabled={isUpdating}
          title={isUpdating ? "Запазване..." : "Промени крайния срок"}
        />
      </div>
    </div>
  );
};

export default SidebarDates;