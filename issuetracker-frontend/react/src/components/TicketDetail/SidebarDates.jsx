import React from 'react';

const SidebarDates = ({ createDate, updateDate, dueDate, onDueDateChange, isUpdating }) => {

  const formatForDisplay = (dateString) => {
    if (!dateString) return 'Няма данни';
    const date = new Date(dateString);
    return date.toLocaleDateString('bg-BG', { day: 'numeric', month: 'short', year: 'numeric' });
  };

  const formatForInput = (dateString) => {
    if (!dateString) return '';
    // Взимаме само частта преди "T" (ако датата идва като 2026-05-17T10:23:11)
    return dateString.split('T')[0];
  };

  const handleDateChange = (e) => {
    const selectedDate = e.target.value; // Идва във формат YYYY-MM-DD
    
    if (selectedDate) {
      const isoString = new Date(selectedDate).toISOString();
      onDueDateChange(isoString);
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
          type="date"
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