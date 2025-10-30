# EduSmart Frontend

Modern React frontend for the EduSmart Learning Management System.

## 🚀 Tech Stack

- **React 18** with TypeScript
- **Vite** - Fast build tool
- **TailwindCSS** - Utility-first CSS
- **React Router** - Navigation
- **Zustand** - State management
- **React Hook Form** - Form handling
- **Axios** - HTTP client
- **Lucide React** - Beautiful icons

## 📦 Installation

```bash
# Navigate to frontend directory
cd frontend

# Install dependencies
npm install

# Start development server
npm run dev
```

## 🛠️ Available Scripts

- `npm run dev` - Start development server (http://localhost:3000)
- `npm run build` - Build for production
- `npm run preview` - Preview production build
- `npm run lint` - Run ESLint

## 🏗️ Project Structure

```
frontend/
├── src/
│   ├── components/        # Reusable components
│   ├── layouts/          # Page layouts
│   ├── pages/            # Page components
│   │   ├── auth/        # Login, Register
│   │   ├── public/      # Public pages
│   │   ├── student/     # Student dashboard
│   │   ├── instructor/  # Instructor dashboard
│   │   └── admin/       # Admin dashboard
│   ├── store/           # Zustand stores
│   ├── lib/             # Utilities & API client
│   ├── App.tsx          # Main app component
│   └── main.tsx         # Entry point
├── public/              # Static assets
└── index.html          # HTML template
```

## 🎨 Features

### Public Pages
- ✅ Modern landing page
- ✅ Course catalog with search/filter
- ✅ Course details page
- ✅ Login & Registration

### Student Portal
- ✅ Dashboard with progress
- ✅ My courses
- ✅ Course player
- ✅ Shopping cart
- ✅ Checkout process

### Instructor Portal
- ✅ Dashboard with analytics
- ✅ Course management
- ✅ Create/edit courses
- ✅ Student management

### Admin Portal
- ✅ Admin dashboard
- ✅ User management
- ✅ Course management
- ✅ Analytics & reports

## 🔐 Authentication

The app uses JWT-based authentication stored in localStorage via Zustand persist middleware.

## 🌐 API Integration

API calls are made through the configured Axios instance in `src/lib/api.ts` which:
- Adds auth tokens automatically
- Handles 401 redirects
- Proxies to backend at `/api`

## 🎨 Styling

TailwindCSS is configured with:
- Custom color palette (primary blue theme)
- Utility classes for buttons, cards, inputs
- Responsive design
- Dark mode support (optional)

## 📱 Responsive Design

The UI is fully responsive and works on:
- 📱 Mobile (320px+)
- 📱 Tablet (768px+)
- 💻 Desktop (1024px+)
- 🖥️ Large screens (1280px+)

## 🚀 Deployment

### Build for Production

```bash
npm run build
```

The build output will be in the `dist` directory.

### Deploy to Netlify

```bash
# Install Netlify CLI
npm install -g netlify-cli

# Deploy
netlify deploy --prod --dir=dist
```

### Deploy to Vercel

```bash
# Install Vercel CLI
npm install -g vercel

# Deploy
vercel --prod
```

## 🔧 Environment Variables

Create a `.env` file:

```env
VITE_API_URL=http://localhost:8080/api
```

## 📝 Development Tips

1. **Hot Module Replacement (HMR)** is enabled for instant updates
2. **TypeScript** provides type safety
3. **ESLint** helps maintain code quality
4. Use **React DevTools** for debugging

## 🤝 Contributing

1. Follow the existing code structure
2. Use TypeScript for type safety
3. Follow the component naming conventions
4. Keep components small and focused
5. Write reusable utility functions

## 📚 Learn More

- [React Documentation](https://react.dev/)
- [Vite Documentation](https://vitejs.dev/)
- [TailwindCSS Documentation](https://tailwindcss.com/)
- [React Router Documentation](https://reactrouter.com/)
- [Zustand Documentation](https://zustand-demo.pmnd.rs/)

## 🎉 Status

✅ **COMPLETE AND PRODUCTION READY!**

All core features implemented and tested.
