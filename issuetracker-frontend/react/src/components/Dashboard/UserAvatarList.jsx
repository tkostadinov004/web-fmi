const getInitials = (username) =>
  username
    .split(" ")
    .map((part) => part[0])
    .join("");

export const UserAvatar = ({ user }) => (
  <div className="user-avatar" title={user.username}>
    {user.profilePicturePath
      ? <img src={user.profilePicturePath} alt={user.username} />
      : getInitials(user.username)
    }
  </div>
);

export const UserAvatarList = ({ users }) => (
  <div className="users-list">
    {users.map((user) => (
      <UserAvatar key={user.username} user={user} />
    ))}
  </div>
);
