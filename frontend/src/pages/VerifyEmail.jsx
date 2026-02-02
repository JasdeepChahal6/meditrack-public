import { useEffect, useState, useRef } from 'react';
import { useNavigate, useSearchParams } from 'react-router-dom';
import api from '../services/api';

const VerifyEmail = () => {
  const [searchParams] = useSearchParams();
  const navigate = useNavigate();
  const [status, setStatus] = useState('verifying');
  const [message, setMessage] = useState('');
  const hasVerified = useRef(false);

  useEffect(() => {
    if (hasVerified.current) return; // Prevent duplicate requests
    hasVerified.current = true;
    const verifyToken = async () => {
      const token = searchParams.get('token');
      if (!token) {
        setStatus('error');
        setMessage('Invalid verification link.');
        return;
      }

      try {
        const res = await api.get('/auth/verify-email', { params: { token } });
        
        // Check for success status codes
        if (res.status === 200 || res.status === 204) {
          setStatus('success');
          const msg = res.data?.message || res.data || 'Email verified successfully!';
          setMessage(typeof msg === 'string' ? msg : 'Email verified successfully!');
          setTimeout(() => navigate('/login'), 2000);
        } else {
          setStatus('error');
          setMessage('Unexpected response from server.');
        }
      } catch (err) {
        const msg = err?.response?.data || 'Verification failed. Token may be expired or invalid.';
        const msgStr = typeof msg === 'string' ? msg : 'Verification failed.';
        
        // Handle already verified case
        if (msgStr.toLowerCase().includes('already verified')) {
          setStatus('success');
          setMessage('Email is already verified. You can login now.');
          setTimeout(() => navigate('/login'), 2000);
        } else {
          setStatus('error');
          setMessage(msgStr);
        }
      }
    };

    verifyToken();
  }, [searchParams, navigate]);

  return (
    <div className="mx-auto flex min-h-screen max-w-md flex-col justify-center px-4">
      <div className="rounded-lg bg-white p-6 shadow-sm text-center">
        {status === 'verifying' && (
          <>
            <div className="mx-auto mb-4 h-12 w-12 animate-spin rounded-full border-4 border-slate-200 border-t-slate-900"></div>
            <h1 className="text-xl font-semibold text-slate-900">Verifying Email...</h1>
            <p className="mt-2 text-sm text-slate-600">Please wait while we verify your email address.</p>
          </>
        )}
        {status === 'success' && (
          <>
            <div className="mx-auto mb-4 flex h-12 w-12 items-center justify-center rounded-full bg-green-100">
              <svg className="h-6 w-6 text-green-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M5 13l4 4L19 7" />
              </svg>
            </div>
            <h1 className="text-xl font-semibold text-slate-900">Email Verified!</h1>
            <p className="mt-2 text-sm text-slate-600">{message}</p>
            <p className="mt-4 text-sm text-slate-500">Redirecting to login...</p>
          </>
        )}
        {status === 'error' && (
          <>
            <div className="mx-auto mb-4 flex h-12 w-12 items-center justify-center rounded-full bg-red-100">
              <svg className="h-6 w-6 text-red-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M6 18L18 6M6 6l12 12" />
              </svg>
            </div>
            <h1 className="text-xl font-semibold text-slate-900">Verification Failed</h1>
            <p className="mt-2 text-sm text-red-600">{message}</p>
            <button
              onClick={() => navigate('/login')}
              className="mt-6 rounded bg-slate-900 px-4 py-2 text-sm font-medium text-white transition hover:bg-slate-800"
            >
              Go to Login
            </button>
          </>
        )}
      </div>
    </div>
  );
};

export default VerifyEmail;
