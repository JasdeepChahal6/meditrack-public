import { useState } from 'react';
import { Link } from 'react-router-dom';
import api from '../services/api';

const ForgotPassword = () => {
  const [email, setEmail] = useState('');
  const [status, setStatus] = useState('');
  const [submitting, setSubmitting] = useState(false);

  const handleSubmit = async (e) => {
    e.preventDefault();
    setSubmitting(true);
    setStatus('');
    try {
      await api.post('/auth/forgot-password', null, { params: { email } });
      setStatus('success');
    } catch (err) {
      setStatus('success'); // Always show success for security (don't leak if email exists)
    } finally {
      setSubmitting(false);
    }
  };

  return (
    <div className="mx-auto flex min-h-screen max-w-md flex-col justify-center px-4">
      <h1 className="mb-6 text-2xl font-semibold text-slate-900">Forgot Password</h1>
      
      {status === 'success' ? (
        <div className="rounded-lg bg-white p-6 shadow-sm">
          <div className="mx-auto mb-4 flex h-12 w-12 items-center justify-center rounded-full bg-green-100">
            <svg className="h-6 w-6 text-green-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M5 13l4 4L19 7" />
            </svg>
          </div>
          <h2 className="text-center text-lg font-semibold text-slate-900">Check Your Email</h2>
          <p className="mt-2 text-center text-sm text-slate-600">
            If an account exists for <span className="font-medium">{email}</span>, we've sent a password reset link.
          </p>
          <Link
            to="/login"
            className="mt-6 block w-full rounded bg-slate-900 px-4 py-2 text-center text-sm font-medium text-white transition hover:bg-slate-800"
          >
            Back to Login
          </Link>
        </div>
      ) : (
        <form onSubmit={handleSubmit} className="space-y-4 rounded-lg bg-white p-6 shadow-sm">
          <p className="text-sm text-slate-600">
            Enter your email address and we'll send you a link to reset your password.
          </p>
          <div className="space-y-2">
            <label className="block text-sm font-medium text-slate-700" htmlFor="email">Email Address</label>
            <input
              id="email"
              name="email"
              type="email"
              value={email}
              onChange={(e) => setEmail(e.target.value)}
              required
              className="w-full rounded border border-slate-200 px-3 py-2 text-sm shadow-sm focus:border-slate-400"
            />
          </div>
          <button
            type="submit"
            disabled={submitting}
            className="w-full rounded bg-slate-900 px-4 py-2 text-sm font-medium text-white transition hover:bg-slate-800 disabled:opacity-60"
          >
            {submitting ? 'Sending...' : 'Send Reset Link'}
          </button>
          <p className="text-center text-sm text-slate-600">
            Remember your password?{' '}
            <Link to="/login" className="font-medium text-blue-600 hover:text-blue-700">Login</Link>
          </p>
        </form>
      )}
    </div>
  );
};

export default ForgotPassword;
