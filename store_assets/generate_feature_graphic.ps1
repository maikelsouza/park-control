Add-Type -AssemblyName System.Drawing
$width = 1024
$height = 500
$bmp = New-Object System.Drawing.Bitmap $width, $height, ([System.Drawing.Imaging.PixelFormat]::Format24bppRgb)
$gfx = [System.Drawing.Graphics]::FromImage($bmp)
$gfx.SmoothingMode = [System.Drawing.Drawing2D.SmoothingMode]::AntiAlias
$gfx.TextRenderingHint = [System.Drawing.Text.TextRenderingHint]::AntiAliasGridFit
$gfx.PixelOffsetMode = [System.Drawing.Drawing2D.PixelOffsetMode]::HighQuality
# Diagonal gradient background (brand blue tones)
$colorTop = [System.Drawing.ColorTranslator]::FromHtml("#1976D2")
$colorBottom = [System.Drawing.ColorTranslator]::FromHtml("#0D47A1")
$rect = New-Object System.Drawing.Rectangle 0, 0, $width, $height
$brush = New-Object System.Drawing.Drawing2D.LinearGradientBrush($rect, $colorTop, $colorBottom, 45.0)
$gfx.FillRectangle($brush, $rect)
# --- Left icon badge: rounded white circle with blue "P" (mirrors app icon) ---
$badgeSize = 300
$badgeX = 80
$badgeY = ($height - $badgeSize) / 2
$circleBrush = New-Object System.Drawing.SolidBrush ([System.Drawing.Color]::White)
$gfx.FillEllipse($circleBrush, $badgeX, $badgeY, $badgeSize, $badgeSize)
# Build the "P" path in 108-unit space (same geometry as ic_launcher_foreground.xml), scaled & centered into the badge
$scale = ($badgeSize * 0.62) / 108.0
$path = New-Object System.Drawing.Drawing2D.GraphicsPath
$path.FillMode = [System.Drawing.Drawing2D.FillMode]::Winding
$path.AddRectangle((New-Object System.Drawing.RectangleF 36, 28, 12, 54))
$path.StartFigure()
$path.AddLine(36, 28, 62, 28)
$path.AddArc(52, 28, 20, 20, -90, 90)
$path.AddLine(72, 38, 72, 48)
$path.AddArc(52, 38, 20, 20, 0, 90)
$path.AddLine(72, 58, 36, 58)
$path.CloseFigure()
$path.StartFigure()
$path.AddLine(48, 36, 48, 50)
$path.AddLine(48, 50, 62, 50)
$path.AddLine(62, 50, 62, 36)
$path.CloseFigure()
$bounds = $path.GetBounds()
$matrix = New-Object System.Drawing.Drawing2D.Matrix
$matrix.Scale($scale, $scale)
$path.Transform($matrix)
$scaledBounds = $path.GetBounds()
$offsetX = $badgeX + ($badgeSize - $scaledBounds.Width) / 2 - $scaledBounds.X
$offsetY = $badgeY + ($badgeSize - $scaledBounds.Height) / 2 - $scaledBounds.Y
$translate = New-Object System.Drawing.Drawing2D.Matrix
$translate.Translate($offsetX, $offsetY)
$path.Transform($translate)
$pBrush = New-Object System.Drawing.SolidBrush ([System.Drawing.ColorTranslator]::FromHtml("#0D47A1"))
$gfx.FillPath($pBrush, $path)
# --- Right side: App name + tagline ---
$textX = $badgeX + $badgeSize + 60
$titleFont = New-Object System.Drawing.Font("Arial", 72, [System.Drawing.FontStyle]::Bold)
$subFont = New-Object System.Drawing.Font("Arial", 25, [System.Drawing.FontStyle]::Regular)
$whiteBrush = New-Object System.Drawing.SolidBrush ([System.Drawing.Color]::White)
$lightBrush = New-Object System.Drawing.SolidBrush ([System.Drawing.ColorTranslator]::FromHtml("#E3F2FD"))
$titleText = "ParkControl"
$titleSize = $gfx.MeasureString($titleText, $titleFont)
$titleY = ($height / 2) - $titleSize.Height - 5
$gfx.DrawString($titleText, $titleFont, $whiteBrush, $textX, $titleY)
$subText = "Controle de entrada e saida de veiculos"
$subY = $titleY + $titleSize.Height + 8
$gfx.DrawString($subText, $subFont, $lightBrush, $textX, $subY)
$outFile = "C:\Maikel\Projects\ParkControl\store_assets\feature_graphic_1024x500.png"
$bmp.Save($outFile, [System.Drawing.Imaging.ImageFormat]::Png)
$gfx.Dispose()
$bmp.Dispose()
Write-Output "Saved: $outFile"
