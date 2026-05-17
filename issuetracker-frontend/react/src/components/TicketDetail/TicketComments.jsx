import React, { useState, useEffect } from 'react';
import CommentItem from './CommentItem';
import { ticketService } from '../../services/ticketService';

const TicketComments = ({ ticketCode }) => {
  const [comments, setComments] = useState([]);
  const [isLoading, setIsLoading] = useState(true);
  const [newCommentText, setNewCommentText] = useState('');
  const [isPosting, setIsPosting] = useState(false);

  const fetchComments = async () => {
    setIsLoading(true);
    try {
      const data = await ticketService.getComments(ticketCode);
      setComments(data);
    } catch (error) {
      console.error('Сървърна грешка:', error);
    } finally {
      setIsLoading(false);
    }
  };

  useEffect(() => {
    if (ticketCode) {
      fetchComments();
    }
  }, [ticketCode]);

  const handlePostComment = async () => {
    if (!newCommentText.trim()) return;

    setIsPosting(true);
    try {
      await ticketService.addComment(ticketCode, newCommentText);
      setNewCommentText('');
      fetchComments();
    } catch (error) {
      console.error('Грешка при добавяне:', error);
    } finally {
      setIsPosting(false);
    }
  };

  const handleCommentUpdatedOrDeleted = () => {
    fetchComments();
  };

  return (
    <div className="ticket-comments-section">
      
      <div className="add-comment-box">
        <div className="add-comment-avatar">
           {/* Тук по-късно може да сложим профилната снимка на логнатия потребител */}
          <i className="fa-solid fa-user"></i>
        </div>
        <div className="add-comment-input-area">
          <textarea
            className="comment-textarea"
            placeholder="Добави коментар..."
            value={newCommentText}
            onChange={(e) => setNewCommentText(e.target.value)}
            disabled={isPosting}
          />

          {newCommentText.trim() && (
            <div className="comment-actions">
              <button 
                className="save-btn" 
                onClick={handlePostComment}
                disabled={isPosting}
              >
                {isPosting ? 'Запазване...' : 'Запази'}
              </button>
              <button 
                className="cancel-btn" 
                onClick={() => setNewCommentText('')}
                disabled={isPosting}
              >
                Отказ
              </button>
            </div>
          )}
        </div>
      </div>

      <div className="comments-list">
        {isLoading ? (
          <p className="loading-text">Зареждане на коментари...</p>
        ) : comments.length > 0 ? (
          comments.map((comment) => (
            <CommentItem 
              key={comment.uuid} 
              comment={comment} 
              onUpdate={handleCommentUpdatedOrDeleted}
              onDelete={handleCommentUpdatedOrDeleted}
            />
          ))
        ) : (
          <p className="no-comments-text">Няма добавени коментари към този билет.</p>
        )}
      </div>

    </div>
  );
};

export default TicketComments;