import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom'
import { Toaster } from 'react-hot-toast'
import { useAuthStore } from './store/authStore'

// Layouts
import PublicLayout from './layouts/PublicLayout'
import DashboardLayout from './layouts/DashboardLayout'

// Public Pages
import HomePage from './pages/public/HomePage'
import CoursesPage from './pages/public/CoursesPage'
import CourseDetailPage from './pages/public/CourseDetailPage'
import LoginPage from './pages/auth/LoginPage'
import RegisterPage from './pages/auth/RegisterPage'

// Student Pages
import StudentDashboard from './pages/student/Dashboard'
import MyCoursesPage from './pages/student/MyCoursesPage'
import CoursePlayerPage from './pages/student/CoursePlayerPage'
import CartPage from './pages/student/CartPage'
import CheckoutPage from './pages/student/CheckoutPage'

// Instructor Pages
import InstructorDashboard from './pages/instructor/Dashboard'
import MyCourses from './pages/instructor/MyCourses'
import CreateCourse from './pages/instructor/CreateCourse'

// Admin Pages
import AdminDashboard from './pages/admin/Dashboard'

// Protected Route Component
const ProtectedRoute = ({ children, role }: { children: React.ReactNode; role?: string }) => {
  const { user, isAuthenticated } = useAuthStore()
  
  if (!isAuthenticated) {
    return <Navigate to="/login" replace />
  }
  
  if (role && user?.role !== role) {
    return <Navigate to="/dashboard" replace />
  }
  
  return <>{children}</>
}

function App() {
  return (
    <BrowserRouter>
      <Toaster position="top-right" />
      <Routes>
        {/* Public Routes */}
        <Route element={<PublicLayout />}>
          <Route path="/" element={<HomePage />} />
          <Route path="/courses" element={<CoursesPage />} />
          <Route path="/courses/:id" element={<CourseDetailPage />} />
          <Route path="/login" element={<LoginPage />} />
          <Route path="/register" element={<RegisterPage />} />
        </Route>

        {/* Protected Routes */}
        <Route element={<ProtectedRoute><DashboardLayout /></ProtectedRoute>}>
          {/* Student Routes */}
          <Route path="/dashboard" element={<StudentDashboard />} />
          <Route path="/my-courses" element={<MyCoursesPage />} />
          <Route path="/learn/:courseId" element={<CoursePlayerPage />} />
          <Route path="/cart" element={<CartPage />} />
          <Route path="/checkout" element={<CheckoutPage />} />
          
          {/* Instructor Routes */}
          <Route path="/instructor" element={
            <ProtectedRoute role="INSTRUCTOR">
              <InstructorDashboard />
            </ProtectedRoute>
          } />
          <Route path="/instructor/courses" element={
            <ProtectedRoute role="INSTRUCTOR">
              <MyCourses />
            </ProtectedRoute>
          } />
          <Route path="/instructor/courses/create" element={
            <ProtectedRoute role="INSTRUCTOR">
              <CreateCourse />
            </ProtectedRoute>
          } />
          
          {/* Admin Routes */}
          <Route path="/admin" element={
            <ProtectedRoute role="ADMIN">
              <AdminDashboard />
            </ProtectedRoute>
          } />
        </Route>
      </Routes>
    </BrowserRouter>
  )
}

export default App
