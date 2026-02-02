import { useEffect, useState } from 'react';
import api from '../services/api';
import { useAuth } from '../context/AuthContext';

const Profile = () => {
  const { user, setUser } = useAuth();

  const [profileForm, setProfileForm] = useState({ name: '', email: '' });
  const [passwordForm, setPasswordForm] = useState({ currentPassword: '', newPassword: '' });
  const [status, setStatus] = useState('');
  const [error, setError] = useState('');

  useEffect(() => {
    if (user) {
      setProfileForm({ name: user.name || '', email: user.email || '' });
    }
  }, [user]);

  const handleProfileChange = (e) => {
    setProfileForm({ ...profileForm, [e.target.name]: e.target.value });
  };

  const handlePasswordChange = (e) => {
    setPasswordForm({ ...passwordForm, [e.target.name]: e.target.value });
  };

  const submitProfile = async (e) => {
    e.preventDefault();
    setStatus('');
    setError('');
    try {
      const res = await api.patch('/user/profile', profileForm);
      setUser(res.data);
      setStatus('Profile updated successfully.');
    } catch (err) {
      const message = err?.response?.data || 'Update failed. Please try again.';
      setError(typeof message === 'string' ? message : 'Update failed.');
    }
  };

  const submitPassword = async (e) => {
    e.preventDefault();
    setStatus('');
    setError('');
    try {
      await api.post('/user/change-password', passwordForm);
      setStatus('Password changed successfully. A confirmation email has been sent.');
      setPasswordForm({ currentPassword: '', newPassword: '' });
    } catch (err) {
      const message = err?.response?.data || 'Password change failed.';
      setError(typeof message === 'string' ? message : 'Password change failed.');
    }
  };

  return (
    <div className="mx-auto max-w-3xl px-4 py-8">
      <h1 className="text-2xl font-semibold text-slate-900">Profile</h1>
      <p className="text-sm text-slate-600">Manage your account details.</p>

      <div className="mt-6 grid gap-6 md:grid-cols-2">
        <form onSubmit={submitProfile} className="space-y-4 rounded-lg border border-slate-200 bg-white p-5 shadow-sm">
          <h2 className="text-lg font-semibold text-slate-900">Profile Info</h2>
          <div className="space-y-2">
            <label className="block text-sm font-medium text-slate-700" htmlFor="name">Name</label>
            <input
              id="name"
              name="name"
              type="text"
              value={profileForm.name}
              onChange={handleProfileChange}
              required
              className="w-full rounded border border-slate-200 px-3 py-2 text-sm focus:border-slate-400"
            />
          </div>
          <div className="space-y-2">
            <label className="block text-sm font-medium text-slate-700" htmlFor="email">Email</label>
            <input
              id="email"
              name="email"
              type="email"
              disabled
              value={profileForm.email}
              onChange={handleProfileChange}
              className="w-full rounded border border-slate-200 bg-slate-50 px-3 py-2 text-sm text-slate-500 cursor-not-allowed"
            />
            <p className="text-xs text-slate-500">Email cannot be changed for security reasons</p>
          </div>
          <button
            type="submit"
            className="w-full rounded bg-slate-900 px-4 py-2 text-sm font-medium text-white transition hover:bg-slate-800"
          >
            Save Changes
          </button>
        </form>

        <form onSubmit={submitPassword} className="space-y-4 rounded-lg border border-slate-200 bg-white p-5 shadow-sm">
          <h2 className="text-lg font-semibold text-slate-900">Change Password</h2>
          <div className="space-y-2">
            <label className="block text-sm font-medium text-slate-700" htmlFor="currentPassword">Current Password</label>
            <input
              id="currentPassword"
              name="currentPassword"
              type="password"
              value={passwordForm.currentPassword}
              onChange={handlePasswordChange}
              required
              className="w-full rounded border border-slate-200 px-3 py-2 text-sm focus:border-slate-400"
            />
          </div>
          <div className="space-y-2">
            <label className="block text-sm font-medium text-slate-700" htmlFor="newPassword">New Password</label>
            <input
              id="newPassword"
              name="newPassword"
              type="password"
              value={passwordForm.newPassword}
              onChange={handlePasswordChange}
              required
              className="w-full rounded border border-slate-200 px-3 py-2 text-sm focus:border-slate-400"
            />
          </div>
          <button
            type="submit"
            className="w-full rounded bg-slate-900 px-4 py-2 text-sm font-medium text-white transition hover:bg-slate-800"
          >
            Update Password
          </button>
        </form>
      </div>

      {(status || error) && (
        <div className="mt-4 rounded border border-slate-200 bg-white p-4 text-sm shadow-sm">
          {status && <p className="text-green-700">{status}</p>}
          {error && <p className="text-red-600">{error}</p>}
        </div>
      )}
    </div>
  );
};

export default Profile;
