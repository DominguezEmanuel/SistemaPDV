const { app, BrowserWindow } = require("electron");

const path = require("path");

function createWindow() {
  const win = new BrowserWindow({
    width: 1400,
    height: 900,

    webPreferences: {
      nodeIntegration: false,
      contextIsolation: true,
    },

    icon: path.join(__dirname, "../public/logo.ico"),

    titleBarStyle: "hidden",

    titleBarOverlay: {
      color: "#2f3241",
      symbolColor: "#74b1be",
      height: 60,
    },
  });

  win.loadURL("http://localhost:4200");
}

app.whenReady().then(() => {
  createWindow();
});
