import React, { useState } from 'react';

const CommentItem = ({ comment, onUpdate, onDelete }) => {
  const [isEditing, setIsEditing] = useState(false);
  const [editText, setEditText] = useState(comment.content);
  const [isSubmitting, setIsSubmitting] = useState(false);

  const formatDateTime = (dateString) => {
    const date = new Date(dateString);
    return date.toLocaleString('bg-BG', { 
      day: 'numeric', month: 'short', year: 'numeric', 
      hour: '2-digit', minute: '2-digit' 
    });
  };

  const handleSaveEdit = async () => {
    if (!editText.trim() || editText === comment.content) {
      setIsEditing(false);
      return;
    }

    setIsSubmitting(true);
    try {
      const token = localStorage.getItem('authToken');
      const response = await fetch(`http://localhost:8080/comments/${comment.uuid}`, {
        method: 'PATCH',
        headers: {
          'Content-Type': 'application/json',
          'Authorization': `Bearer ${token}`
        },
        body: JSON.stringify({ content: editText })
      });

      if (response.ok) {
        setIsEditing(false);
        onUpdate();
      } else {
        alert('Грешка при редактиране на коментара.');
      }
    } catch (error) {
      console.error(error);
    } finally {
      setIsSubmitting(false);
    }
  };

  const handleDelete = async () => {
    const confirmDelete = window.confirm('Сигурни ли сте, че искате да изтриете този коментар?');
    if (!confirmDelete) return;

    try {
      const token = localStorage.getItem('authToken');
      const response = await fetch(`http://localhost:8080/comments/${comment.uuid}`, {
        method: 'DELETE',
        headers: { 'Authorization': `Bearer ${token}` }
      });

      if (response.ok) {
        onDelete();
      } else if (response.status === 403) {
        alert('Нямате права да изтриете чужд коментар.');
      } else {
        alert('Възникна грешка при изтриването.');
      }
    } catch (error) {
      console.error(error);
    }
  };

  return (
    <div className="comment-item">
      <div className="comment-avatar">
        {(comment.authorUsername || 'U').charAt(0).toUpperCase()}
      </div>
      
      <div className="comment-content-area">
        <div className="comment-header">
          <span className="comment-author">{comment.authorUsername}</span>
          <span className="comment-date">{formatDateTime(comment.createDate)}</span>
          
          {!isEditing && (
            <div className="comment-item-actions">
              <button onClick={() => setIsEditing(true)}>Редактирай</button>
              <button onClick={handleDelete} className="delete-text-btn">Изтрий</button>
            </div>
          )}
        </div>

        <div className="comment-body">
          {isEditing ? (
            <div className="editable-description-container">
              <textarea
                className="description-textarea"
                value={editText}
                onChange={(e) => setEditText(e.target.value)}
                disabled={isSubmitting}
                autoFocus
              />
              <div className="editable-actions">
                <button 
                  className="save-btn" 
                  onClick={handleSaveEdit}
                  disabled={isSubmitting}
                >
                  {isSubmitting ? 'Запазване...' : 'Запази'}
                </button>
                <button 
                  className="cancel-btn" 
                  onClick={() => {
                    setEditText(comment.content);
                    setIsEditing(false);
                  }}
                  disabled={isSubmitting}
                >
                  Отказ
                </button>
              </div>
            </div>
          ) : (
            <p>{comment.content}</p>
          )}
        </div>
      </div>
    </div>
  );
};

export default CommentItem;