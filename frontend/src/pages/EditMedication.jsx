import { useEffect, useState } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import api from '../services/api';

const EditMedication = () => {
  const { id } = useParams();
  const navigate = useNavigate();
  const [med, setMed] = useState(null);
  const [form, setForm] = useState({ dosageAmount: '', dosageUnit: 'mg', frequency: '', startDate: '', instructions: '' });
  const [customUnit, setCustomUnit] = useState('');
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [saving, setSaving] = useState(false);

  const dosageUnits = [
    'mg', 'mcg', 'g', 'ml', 'L', 'tablet', 'tablets', 'capsule', 'capsules',
    'drop', 'drops', 'spray', 'sprays', 'puff', 'puffs', 'patch', 'IU', 'Other'
  ];

  useEffect(() => {
    const loadMedication = async () => {
      setLoading(true);
      setError('');
      try {
        const res = await api.get('/user-medications/me');
        const found = (res.data || []).find((item) => String(item.id) === String(id));
        if (!found) {
          setError('Medication not found.');
          setLoading(false);
          return;
        }
        setMed(found);
        
        // Split dosage into amount and unit
        const dosageStr = found.dosage || '';
        const parts = dosageStr.trim().split(/\s+/);
        let amount = '';
        let unit = 'mg';
        
        if (parts.length >= 2) {
          amount = parts[0];
          unit = parts.slice(1).join(' ');
        } else if (parts.length === 1) {
          // If only one part, try to extract number
          const match = dosageStr.match(/^([\d.]+)\s*(.*)$/);
          if (match) {
            amount = match[1];
            unit = match[2] || 'mg';
          } else {
            amount = dosageStr;
          }
        }
        
        // Check if unit is in our list, otherwise set to Other
        const isKnownUnit = dosageUnits.slice(0, -1).includes(unit); // exclude 'Other'
        if (!isKnownUnit && unit) {
          setCustomUnit(unit);
          unit = 'Other';
        }
        
        setForm({
          dosageAmount: amount,
          dosageUnit: isKnownUnit ? unit : (unit ? 'Other' : 'mg'),
          frequency: found.frequency || '',
          startDate: found.startDate || '',
          instructions: found.instructions || '',
        });
      } catch (err) {
        const message = err?.response?.data || 'Could not load medication.';
        setError(typeof message === 'string' ? message : 'Could not load medication.');
      } finally {
        setLoading(false);
      }
    };

    loadMedication();
  }, [id]);

  const handleChange = (e) => {
    setForm({ ...form, [e.target.name]: e.target.value });
  };

  const submit = async (e) => {
    e.preventDefault();
    
    // Combine dosage amount and unit
    const unit = form.dosageUnit === 'Other' ? customUnit : form.dosageUnit;
    const dosage = `${form.dosageAmount} ${unit}`.trim();
    
    if (!form.dosageAmount || !unit) {
      setError('Please enter both dosage amount and unit.');
      return;
    }
    
    setSaving(true);
    setError('');
    try {
      await api.patch(`/user-medications/${id}`,
        {
          dosage: dosage,
          frequency: form.frequency,
          startDate: form.startDate,
          instructions: form.instructions,
        },
      );
      navigate('/medications');
    } catch (err) {
      const message = err?.response?.data || 'Update failed.';
      setError(typeof message === 'string' ? message : 'Update failed.');
    } finally {
      setSaving(false);
    }
  };

  if (loading) {
    return <div className="flex h-screen items-center justify-center text-slate-600">Loading...</div>;
  }

  if (!med) {
    return (
      <div className="mx-auto max-w-3xl px-4 py-8">
        <p className="text-sm text-red-600">{error || 'Medication not found.'}</p>
      </div>
    );
  }

  return (
    <div className="mx-auto max-w-3xl px-4 py-8">
      <h1 className="text-2xl font-semibold text-slate-900">Edit Medication</h1>
      <p className="text-sm text-slate-600">{med.drugName}</p>

      <form onSubmit={submit} className="mt-6 space-y-4 rounded-lg border border-slate-200 bg-white p-5 shadow-sm">
        <div className="space-y-2">
          <label className="block text-sm font-medium text-slate-700" htmlFor="dosage">Dosage</label>
          <div className="flex gap-2">
            <input
              id="dosageAmount"
              name="dosageAmount"
              type="number"
              step="any"
              value={form.dosageAmount}
              onChange={handleChange}
              placeholder="Amount"
              required
              className="w-1/3 rounded border border-slate-200 px-3 py-2 text-sm focus:border-slate-400"
            />
            <select
              id="dosageUnit"
              name="dosageUnit"
              value={form.dosageUnit}
              onChange={handleChange}
              required
              className="flex-1 rounded border border-slate-200 px-3 py-2 text-sm focus:border-slate-400"
            >
              {dosageUnits.map(unit => (
                <option key={unit} value={unit}>{unit}</option>
              ))}
            </select>
          </div>
          {form.dosageUnit === 'Other' && (
            <input
              type="text"
              value={customUnit}
              onChange={(e) => setCustomUnit(e.target.value)}
              placeholder="Enter custom unit"
              required
              className="w-full rounded border border-slate-200 px-3 py-2 text-sm focus:border-slate-400"
            />
          )}
        </div>

        <div className="space-y-2">
          <label className="block text-sm font-medium text-slate-700" htmlFor="frequency">Frequency</label>
          <input
            id="frequency"
            name="frequency"
            type="text"
            value={form.frequency}
            onChange={handleChange}
            required
            className="w-full rounded border border-slate-200 px-3 py-2 text-sm focus:border-slate-400"
          />
        </div>

        <div className="space-y-2">
          <label className="block text-sm font-medium text-slate-700" htmlFor="startDate">Start Date</label>
          <input
            id="startDate"
            name="startDate"
            type="date"
            value={form.startDate}
            onChange={handleChange}
            required
            className="w-full rounded border border-slate-200 px-3 py-2 text-sm focus:border-slate-400"
          />
        </div>

        <div className="space-y-2">
          <label className="block text-sm font-medium text-slate-700" htmlFor="instructions">Instructions</label>
          <textarea
            id="instructions"
            name="instructions"
            value={form.instructions}
            onChange={handleChange}
            rows={3}
            className="w-full rounded border border-slate-200 px-3 py-2 text-sm focus:border-slate-400"
          />
        </div>

        {error && <p className="text-sm text-red-600">{error}</p>}

        <button
          type="submit"
          disabled={saving}
          className="w-full rounded bg-slate-900 px-4 py-2 text-sm font-medium text-white transition hover:bg-slate-800 disabled:opacity-60"
        >
          {saving ? 'Saving...' : 'Save Changes'}
        </button>
      </form>
    </div>
  );
};

export default EditMedication;
