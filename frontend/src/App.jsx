import { BrowserRouter, Navigate, Route, Routes } from 'react-router-dom';
import Navbar from './components/Navbar';
import PrivateRoute from './components/PrivateRoute';
import { useAuth } from './context/AuthContext';
import AddMedication from './pages/AddMedication';
import Dashboard from './pages/Dashboard';
import DrugLibrary from './pages/DrugLibrary';
import EditMedication from './pages/EditMedication';
import ForgotPassword from './pages/ForgotPassword';
import Login from './pages/Login';
import MedicationList from './pages/MedicationList';
import Profile from './pages/Profile';
import Register from './pages/Register';
import ResetPassword from './pages/ResetPassword';
import VerifyEmail from './pages/VerifyEmail';

const HomeRedirect = () => {
  const { token, loading } = useAuth();
  if (loading) {
    return <div className="flex h-screen items-center justify-center text-slate-600">Loading...</div>;
  }
  return token ? <Navigate to="/dashboard" replace /> : <Navigate to="/login" replace />;
};

function App() {
  return (
    <BrowserRouter>
      <div className="min-h-screen bg-slate-50">
        <Navbar />
        <Routes>
          <Route path="/" element={<HomeRedirect />} />
          <Route path="/login" element={<Login />} />
          <Route path="/register" element={<Register />} />
          <Route path="/verify-email" element={<VerifyEmail />} />
          <Route path="/forgot-password" element={<ForgotPassword />} />
          <Route path="/reset-password" element={<ResetPassword />} />

          <Route element={<PrivateRoute />}>
            <Route path="/dashboard" element={<Dashboard />} />
            <Route path="/drug-library" element={<DrugLibrary />} />
            <Route path="/profile" element={<Profile />} />
            <Route path="/medications" element={<MedicationList />} />
            <Route path="/medications/add" element={<AddMedication />} />
            <Route path="/medications/edit/:id" element={<EditMedication />} />
          </Route>

          <Route path="*" element={<Navigate to="/" replace />} />
        </Routes>
      </div>
    </BrowserRouter>
  );
}

export default App;
