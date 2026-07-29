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
  });

  win.loadURL("http://localhost:4200");
}

app.whenReady().then(createWindow);
