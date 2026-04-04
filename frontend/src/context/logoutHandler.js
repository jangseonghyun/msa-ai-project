let logoutFn = null;

export const setLogoutHandler = (fn) => {
  logoutFn = fn;
}

export const getLogoutHandler = () => logoutFn;