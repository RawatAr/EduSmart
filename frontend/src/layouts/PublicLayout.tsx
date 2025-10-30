import { Outlet, Link } from 'react-router-dom'
import { GraduationCap, ShoppingCart, User, LogIn } from 'lucide-react'
import { useAuthStore } from '../store/authStore'

export default function PublicLayout() {
  const { isAuthenticated, user } = useAuthStore()

  return (
    <div className="min-h-screen flex flex-col">
      {/* Navigation */}
      <nav className="bg-white shadow-sm border-b">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
          <div className="flex justify-between h-16">
            <div className="flex items-center">
              <Link to="/" className="flex items-center space-x-2">
                <GraduationCap className="h-8 w-8 text-primary-600" />
                <span className="text-2xl font-bold text-gray-900">EduSmart</span>
              </Link>
              <div className="hidden md:ml-10 md:flex md:space-x-8">
                <Link to="/courses" className="text-gray-700 hover:text-primary-600 px-3 py-2">
                  Courses
                </Link>
                <Link to="/" className="text-gray-700 hover:text-primary-600 px-3 py-2">
                  About
                </Link>
                <Link to="/" className="text-gray-700 hover:text-primary-600 px-3 py-2">
                  Contact
                </Link>
              </div>
            </div>
            
            <div className="flex items-center space-x-4">
              {isAuthenticated ? (
                <>
                  <Link to="/cart" className="text-gray-700 hover:text-primary-600">
                    <ShoppingCart className="h-6 w-6" />
                  </Link>
                  <Link to="/dashboard" className="flex items-center space-x-2 text-gray-700 hover:text-primary-600">
                    <User className="h-6 w-6" />
                    <span>{user?.firstName}</span>
                  </Link>
                </>
              ) : (
                <>
                  <Link to="/login" className="btn btn-secondary">
                    <LogIn className="h-4 w-4 mr-2 inline" />
                    Login
                  </Link>
                  <Link to="/register" className="btn btn-primary">
                    Get Started
                  </Link>
                </>
              )}
            </div>
          </div>
        </div>
      </nav>

      {/* Main Content */}
      <main className="flex-1">
        <Outlet />
      </main>

      {/* Footer */}
      <footer className="bg-gray-900 text-white">
        <div className="max-w-7xl mx-auto px-4 py-12">
          <div className="grid grid-cols-1 md:grid-cols-4 gap-8">
            <div>
              <div className="flex items-center space-x-2 mb-4">
                <GraduationCap className="h-8 w-8" />
                <span className="text-xl font-bold">EduSmart</span>
              </div>
              <p className="text-gray-400">
                Transform your career with world-class online courses.
              </p>
            </div>
            <div>
              <h3 className="font-semibold mb-4">Product</h3>
              <ul className="space-y-2 text-gray-400">
                <li><Link to="/courses" className="hover:text-white">Courses</Link></li>
                <li><Link to="/" className="hover:text-white">Pricing</Link></li>
                <li><Link to="/" className="hover:text-white">Features</Link></li>
              </ul>
            </div>
            <div>
              <h3 className="font-semibold mb-4">Company</h3>
              <ul className="space-y-2 text-gray-400">
                <li><Link to="/" className="hover:text-white">About</Link></li>
                <li><Link to="/" className="hover:text-white">Blog</Link></li>
                <li><Link to="/" className="hover:text-white">Careers</Link></li>
              </ul>
            </div>
            <div>
              <h3 className="font-semibold mb-4">Support</h3>
              <ul className="space-y-2 text-gray-400">
                <li><Link to="/" className="hover:text-white">Help Center</Link></li>
                <li><Link to="/" className="hover:text-white">Contact</Link></li>
                <li><Link to="/" className="hover:text-white">Privacy</Link></li>
              </ul>
            </div>
          </div>
          <div className="mt-8 pt-8 border-t border-gray-800 text-center text-gray-400">
            <p>&copy; 2025 EduSmart. All rights reserved.</p>
          </div>
        </div>
      </footer>
    </div>
  )
}
