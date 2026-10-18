Add-Type -AssemblyName System.Drawing
$size = 512
$scale = $size / 108.0
$bmp = New-Object System.Drawing.Bitmap $size, $size
$gfx = [System.Drawing.Graphics]::FromImage($bmp)
$gfx.SmoothingMode = [System.Drawing.Drawing2D.SmoothingMode]::AntiAlias
$gfx.PixelOffsetMode = [System.Drawing.Drawing2D.PixelOffsetMode]::HighQuality
# Background fill - matches ic_launcher_background.xml (#1565C0)
$bgColor = [System.Drawing.ColorTranslator]::FromHtml("#1565C0")
$gfx.FillRectangle((New-Object System.Drawing.SolidBrush $bgColor), 0, 0, $size, $size)
# Build the "P" path in the original 108x108 unit space, matching ic_launcher_foreground.xml
$path = New-Object System.Drawing.Drawing2D.GraphicsPath
$path.FillMode = [System.Drawing.Drawing2D.FillMode]::Winding
# Stem rectangle: 36,28 -> 48,82
$path.AddRectangle((New-Object System.Drawing.RectangleF 36, 28, 12, 54))
# Outer loop (P bowl) with rounded right corners (radius 10)
$path.StartFigure()
$path.AddLine(36, 28, 62, 28)
$path.AddArc(52, 28, 20, 20, -90, 90)   # top-right rounded corner -> (72,38)
$path.AddLine(72, 38, 72, 48)
$path.AddArc(52, 38, 20, 20, 0, 90)     # bottom-right rounded corner -> (62,58)
$path.AddLine(72, 58, 36, 58)
$path.CloseFigure()
# Inner hole rectangle (counter-clockwise winding to subtract): 48,36 -> 62,50
$path.StartFigure()
$path.AddLine(48, 36, 48, 50)
$path.AddLine(48, 50, 62, 50)
$path.AddLine(62, 50, 62, 36)
$path.CloseFigure()
$matrix = New-Object System.Drawing.Drawing2D.Matrix
$matrix.Scale($scale, $scale)
$path.Transform($matrix)
$gfx.FillPath((New-Object System.Drawing.SolidBrush ([System.Drawing.Color]::White)), $path)
$outFile = "C:\Maikel\Projects\ParkControl\store_assets\ic_launcher_playstore_512.png"
$bmp.Save($outFile, [System.Drawing.Imaging.ImageFormat]::Png)
$gfx.Dispose()
$bmp.Dispose()
Write-Output "Saved: $outFile"
