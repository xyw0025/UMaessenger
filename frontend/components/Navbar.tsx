'use client';

import Link from 'next/link';
import React, { useState, useEffect } from 'react';
import { ModeToggle } from './mode-toggle';
import { useUserService } from '@/utils';

const Navbar = () => {
  const [nav, setNav] = useState(false);
  const [shadow, setShadow] = useState(false);
  const [navBg, setNavBg] = useState('#DC143C');
  const [linkColor, setLinkColor] = useState('#1f2937');
  const userService = useUserService();
  const { currentUser, logout } = userService;

  async function handleLogout() {
    // setLoggingOut(true);
    await logout();
  }

  //TODO: handle side bar
  // const handleNav = () => {
  //   setNav(!nav);
  // };

  useEffect(() => {
    const handleShadow = () => {
      if (window.scrollY >= 90) {
        setShadow(true);
      } else {
        setShadow(false);
      }
    };
    window.addEventListener('scroll', handleShadow);
  }, []);

  useEffect(() => {
    userService.getCurrent();
  }, []);

  return (
    <div
      className="sticky top-0 bg-[rgb(228,89,89)] shadow-lg shadow-gray-400
      ${
        shadow
          ? 'w-full h-20 shadow-xl z-[100] ease-in-out duration-300'
          : 'w-full h-20 z-[100]'
      }"
    >
      {currentUser ? (
        <div className="flex justify-end items-center w-full h-full px-2 2xl:px-16">
          <div className="mx-10 font-semibold">
            <ul style={{ color: `${linkColor}` }} className="hidden md:flex">
              <li className="ml-10 mt-2 text-lg dark:text-white uppercase hover:border-b">
                <Link href="/">Home</Link>
              </li>
              <li className="ml-10 mt-2 text-lg dark:text-white uppercase hover:border-b">
                <Link href="/profile">Profile</Link>
              </li>
              <button
                onClick={handleLogout}
                className="mx-10 mt-2 text-lg dark:text-white uppercase hover:border-b"
              >
                <span>Logout</span>
              </button>
              <ModeToggle />
            </ul>
          </div>
        </div>
      ) : (
        <div className="flex justify-end items-center w-full h-full px-2 2xl:px-16">
          <div className="mx-10 font-semibold">
            <ul style={{ color: `${linkColor}` }} className="hidden md:flex">
              <li className="ml-10 mt-2 text-lg dark:text-white uppercase hover:border-b">
                <Link href="/">Home</Link>
              </li>
              <li className="ml-10 mt-2 text-lg dark:text-white uppercase hover:border-b">
                <Link href="/about">About</Link>
              </li>
              <li className="ml-10 mt-2 text-lg dark:text-white uppercase hover:border-b">
                <Link href="/demo">Demo</Link>
              </li>
              <li className="mx-10 mt-2 text-lg dark:text-white uppercase hover:border-b">
                <Link href="/login">Login</Link>
              </li>
              <ModeToggle />
            </ul>
          </div>
        </div>
      )}
    </div>
  );
};

export default Navbar;