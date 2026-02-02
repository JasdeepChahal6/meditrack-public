import { Link, useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';

const Navbar = () => {
  const navigate = useNavigate();
  const { user, token, logout } = useAuth();

  const handleLogout = async () => {
    await logout();
    navigate('/login');
  };

  return (
    <nav className="border-b border-slate-200 bg-white/80 backdrop-blur">
      <div className="mx-auto flex max-w-5xl items-center justify-between px-4 py-3">
        <Link to={token ? '/dashboard' : '/login'} className="text-lg font-semibold text-slate-900">
          MediTrack
        </Link>
        {token ? (
          <div className="flex items-center gap-4 text-sm">
            <span className="text-slate-700">Hi, {user?.name || 'User'}</span>
            <div className="flex items-center gap-3">
              <Link to="/dashboard" className="text-slate-600 hover:text-slate-900">Dashboard</Link>
              <Link to="/drug-library" className="text-slate-600 hover:text-slate-900">Drug Library</Link>
              <Link to="/medications" className="text-slate-600 hover:text-slate-900">Medications</Link>
              <Link to="/profile" className="text-slate-600 hover:text-slate-900">Profile</Link>
              <button
                type="button"
                onClick={handleLogout}
                className="rounded bg-slate-900 px-3 py-1.5 text-white transition hover:bg-slate-800"
              >
                Logout
              </button>
            </div>
          </div>
        ) : (
          <div className="flex items-center gap-3 text-sm">
            <Link to="/login" className="text-slate-600 hover:text-slate-900">Login</Link>
            <Link to="/register" className="rounded bg-slate-900 px-3 py-1.5 text-white transition hover:bg-slate-800">
              Register
            </Link>
          </div>
        )}
      </div>
    </nav>
  );
};

export default Navbar;
