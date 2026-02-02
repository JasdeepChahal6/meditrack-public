import { useState } from 'react';
import { Link } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import api from '../services/api';

const Dashboard = () => {
  const { user, logout } = useAuth();
  const [resending, setResending] = useState(false);
  const [resendSuccess, setResendSuccess] = useState(false);

  const handleResendVerification = async () => {
    setResending(true);
    setResendSuccess(false);
    try {
      await api.post('/auth/resend-verification', null, { params: { email: user?.email } });
      setResendSuccess(true);
    } catch (err) {
      const msg = err?.response?.data || '';
      const msgStr = typeof msg === 'string' ? msg : '';
      // If already verified, refresh page to update user state
      if (msgStr.toLowerCase().includes('already verified')) {
        window.location.reload();
      } else {
        console.error('Failed to resend verification email:', err);
      }
    } finally {
      setResending(false);
    }
  };

  return (
    <div className="mx-auto max-w-4xl px-4 py-8">
      {user && !user.emailVerified && (
        <div className="mb-6 rounded-lg border border-yellow-200 bg-yellow-50 p-4">
          <div className="flex items-start justify-between gap-3">
            <div>
              <h3 className="text-sm font-semibold text-yellow-900">Email Verification Pending</h3>
              <p className="mt-1 text-sm text-yellow-800">
                Please check your email and click the verification link to activate your account.
              </p>
              <button
                onClick={handleResendVerification}
                disabled={resending}
                className="mt-2 text-sm font-medium text-yellow-900 hover:text-yellow-700 disabled:opacity-60"
              >
                {resending ? 'Sending...' : 'Resend verification email'}
              </button>
              {resendSuccess && <p className="mt-1 text-sm text-green-700">Verification email sent!</p>}
            </div>
          </div>
        </div>
      )}

      <div className="flex items-center justify-between">
        <div>
          <p className="text-sm text-slate-500">Welcome back</p>
          <h1 className="text-2xl font-semibold text-slate-900">{user?.name || 'User'}</h1>
        </div>
        <button
          onClick={logout}
          className="rounded bg-slate-900 px-4 py-2 text-sm font-medium text-white transition hover:bg-slate-800"
        >
          Logout
        </button>
      </div>

      <div className="mt-8 grid gap-4 sm:grid-cols-2">
        <Link to="/profile" className="rounded-lg border border-slate-200 bg-white p-4 shadow-sm hover:border-slate-300">
          <h2 className="text-lg font-semibold text-slate-900">Profile</h2>
          <p className="text-sm text-slate-600">Update your name, email, and password.</p>
        </Link>
        <Link to="/medications" className="rounded-lg border border-slate-200 bg-white p-4 shadow-sm hover:border-slate-300">
          <h2 className="text-lg font-semibold text-slate-900">Medications</h2>
          <p className="text-sm text-slate-600">View and edit your medications.</p>
        </Link>
        <Link to="/medications/add" className="rounded-lg border border-slate-200 bg-white p-4 shadow-sm hover:border-slate-300">
          <h2 className="text-lg font-semibold text-slate-900">Add Medication</h2>
          <p className="text-sm text-slate-600">Search and add a new medication.</p>
        </Link>
      </div>
    </div>
  );
};

export default Dashboard;
