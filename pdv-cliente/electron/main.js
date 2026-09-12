const { app, BrowserWindow } = require("electron");

const path = require("path");

function createWindow() {
  const mainWindow = new BrowserWindow({
    width: 1280,
    height: 800,

    webPreferences: {
      nodeIntegration: false,
      contextIsolation: true,
    },

    icon: path.join(__dirname, "/assets/logo.ico"),
  });

  if (!app.isPackaged) {
    // Desarrollo
    mainWindow.loadURL("http://localhost:4200");
  } else {
    // Produccion
    mainWindow.loadFile(
      path.join(
        __dirname,
        "..",
        "dist",
        "pdv-cliente",
        "browser",
        "index.html",
      ),
    );
  }
}

app.whenReady().then(() => {
  createWindow();
});

app.on("window-all-closed", () => {
  if (process.platform !== "darwin") {
    app.quit();
  }
});
