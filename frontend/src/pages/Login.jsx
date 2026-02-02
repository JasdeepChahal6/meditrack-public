import { useState } from 'react';
import { Link, useLocation, useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';

const Login = () => {
  const { login } = useAuth();
  const navigate = useNavigate();
  const location = useLocation();
  const redirectPath = location.state?.from?.pathname || '/dashboard';

  const [form, setForm] = useState({ email: '', password: '' });
  const [error, setError] = useState('');
  const [emailNotVerified, setEmailNotVerified] = useState(false);
  const [resending, setResending] = useState(false);
  const [resendSuccess, setResendSuccess] = useState(false);
  const [submitting, setSubmitting] = useState(false);

  const handleChange = (e) => {
    setForm({ ...form, [e.target.name]: e.target.value });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setSubmitting(true);
    setError('');
    setEmailNotVerified(false);
    setResendSuccess(false);
    try {
      await login(form.email, form.password);
      navigate(redirectPath, { replace: true });
    } catch (err) {
      const message = err?.response?.data || 'Login failed. Please try again.';
      const messageStr = typeof message === 'string' ? message : 'Login failed.';
      
      if (err?.response?.status === 403 && messageStr.toLowerCase().includes('verify')) {
        setEmailNotVerified(true);
        setError('Please verify your email before logging in.');
      } else {
        setError(messageStr);
      }
    } finally {
      setSubmitting(false);
    }
  };

  const handleResendVerification = async () => {
    setResending(true);
    setResendSuccess(false);
    try {
      await api.post('/auth/resend-verification', null, { params: { email: form.email } });
      setResendSuccess(true);
    } catch (err) {
      const msg = err?.response?.data || '';
      const msgStr = typeof msg === 'string' ? msg : '';
      if (msgStr.toLowerCase().includes('already verified')) {
        setError('Email is already verified. Please try logging in.');
        setEmailNotVerified(false);
      } else {
        setError('Failed to resend verification email.');
      }
    } finally {
      setResending(false);
    }
  };

  return (
    <div className="mx-auto flex min-h-screen max-w-md flex-col justify-center px-4">
      <h1 className="mb-6 text-2xl font-semibold text-slate-900">Login</h1>
      <form onSubmit={handleSubmit} className="space-y-4 rounded-lg bg-white p-6 shadow-sm">
        <div className="space-y-2">
          <label className="block text-sm font-medium text-slate-700" htmlFor="email">Email</label>
          <input
            id="email"
            name="email"
            type="email"
            value={form.email}
            onChange={handleChange}
            required
            className="w-full rounded border border-slate-200 px-3 py-2 text-sm shadow-sm focus:border-slate-400"
          />
        </div>
        <div className="space-y-2">
          <label className="block text-sm font-medium text-slate-700" htmlFor="password">Password</label>
          <input
            id="password"
            name="password"
            type="password"
            value={form.password}
            onChange={handleChange}
            required
            className="w-full rounded border border-slate-200 px-3 py-2 text-sm shadow-sm focus:border-slate-400"
          />
        </div>
        {error && <p className="text-sm text-red-600">{error}</p>}
        {emailNotVerified && (
          <div className="rounded border border-blue-200 bg-blue-50 p-3">
            <p className="text-sm text-blue-800">Didn't receive the email?</p>
            <button
              type="button"
              onClick={handleResendVerification}
              disabled={resending}
              className="mt-2 text-sm font-medium text-blue-600 hover:text-blue-700 disabled:opacity-60"
            >
              {resending ? 'Sending...' : 'Resend verification email'}
            </button>
            {resendSuccess && <p className="mt-2 text-sm text-green-700">Verification email sent!</p>}
          </div>
        )}
        <button
          type="submit"
          disabled={submitting}
          className="w-full rounded bg-slate-900 px-4 py-2 text-sm font-medium text-white transition hover:bg-slate-800 disabled:opacity-60"
        >
          {submitting ? 'Signing in...' : 'Login'}
        </button>
        <div className="space-y-2 text-center text-sm text-slate-600">
          <p>
            Need an account?{' '}
            <Link to="/register" className="font-medium text-blue-600 hover:text-blue-700">Register</Link>
          </p>
          <p>
            <Link to="/forgot-password" className="font-medium text-blue-600 hover:text-blue-700">Forgot password?</Link>
          </p>
        </div>
      </form>
    </div>
  );
};

export default Login;
