import { Outlet, Link, useNavigate } from 'react-router-dom'
import { 
  GraduationCap, 
  Home, 
  BookOpen, 
  ShoppingCart, 
  User, 
  Settings, 
  LogOut,
  Users,
  BarChart3,
  Video
} from 'lucide-react'
import { useAuthStore } from '../store/authStore'

export default function DashboardLayout() {
  const { user, logout } = useAuthStore()
  const navigate = useNavigate()

  const handleLogout = () => {
    logout()
    navigate('/login')
  }

  const studentNav = [
    { name: 'Dashboard', to: '/dashboard', icon: Home },
    { name: 'My Courses', to: '/my-courses', icon: BookOpen },
    { name: 'Cart', to: '/cart', icon: ShoppingCart },
    { name: 'Profile', to: '/profile', icon: User },
  ]

  const instructorNav = [
    { name: 'Dashboard', to: '/instructor', icon: Home },
    { name: 'My Courses', to: '/instructor/courses', icon: Video },
    { name: 'Create Course', to: '/instructor/courses/create', icon: BookOpen },
    { name: 'Analytics', to: '/instructor/analytics', icon: BarChart3 },
  ]

  const adminNav = [
    { name: 'Dashboard', to: '/admin', icon: Home },
    { name: 'Users', to: '/admin/users', icon: Users },
    { name: 'Courses', to: '/admin/courses', icon: BookOpen },
    { name: 'Analytics', to: '/admin/analytics', icon: BarChart3 },
  ]

  const getNavItems = () => {
    if (user?.role === 'ADMIN') return adminNav
    if (user?.role === 'INSTRUCTOR') return instructorNav
    return studentNav
  }

  return (
    <div className="min-h-screen bg-gray-50 flex">
      {/* Sidebar */}
      <aside className="w-64 bg-white shadow-lg">
        <div className="p-6">
          <Link to="/" className="flex items-center space-x-2">
            <GraduationCap className="h-8 w-8 text-primary-600" />
            <span className="text-xl font-bold">EduSmart</span>
          </Link>
        </div>
        
        <nav className="px-4 space-y-2">
          {getNavItems().map((item) => (
            <Link
              key={item.to}
              to={item.to}
              className="flex items-center space-x-3 px-4 py-3 text-gray-700 hover:bg-primary-50 hover:text-primary-600 rounded-lg transition"
            >
              <item.icon className="h-5 w-5" />
              <span>{item.name}</span>
            </Link>
          ))}
        </nav>

        <div className="absolute bottom-0 w-64 p-4 border-t">
          <div className="flex items-center space-x-3 px-4 py-3">
            <div className="flex-1">
              <p className="text-sm font-medium text-gray-900">
                {user?.firstName} {user?.lastName}
              </p>
              <p className="text-xs text-gray-500">{user?.role}</p>
            </div>
            <button
              onClick={handleLogout}
              className="text-gray-500 hover:text-red-600"
            >
              <LogOut className="h-5 w-5" />
            </button>
          </div>
        </div>
      </aside>

      {/* Main Content */}
      <main className="flex-1 overflow-auto">
        <Outlet />
      </main>
    </div>
  )
}
